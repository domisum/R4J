package no.stelar7.api.r4j.basic.ratelimiting;


import io.domisum.lib.auxiliumlib.TrickleRateLimiter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class TrickleMultiRateLimiter extends RateLimiter
{
	
	// STATE
	private final ReentrantLock lock = new ReentrantLock();
	private final Map<RateLimit, TrickleRateLimiter> trickles = new HashMap<>();
	
	
	// INIT
	public TrickleMultiRateLimiter(List<RateLimit> limits)
	{
		super(limits.toArray(new RateLimit[0]));
	}
	
	
	// INTERFACE
	@Override
	public void acquire()
	{
		lock.lock();
		try
		{
			long delayMs = getDelay();
			if(delayMs > 0)
				Thread.sleep(delayMs);
		}
		catch(InterruptedException ignored) {}
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	public void updatePermitsTakenPerX(Map<Integer, Integer> data) {}
	
	
	// INTERNAL
	private long getDelay()
	{
		long delayMs = limits.stream()
			.map(l -> trickles.computeIfAbsent(l, this::initRateLimiter))
			.mapToLong(trl -> trl.forceAcquireAndReturnDelay().toMillis())
			.max()
			.orElse(0);
		return delayMs + (delayMs > 0 ? 100 : 0);
	}
	
	private TrickleRateLimiter initRateLimiter(RateLimit rateLimit)
	{
		double perSecond = rateLimit.getPermits() * 1000d / rateLimit.getTimeframeInMS() * 0.95;
		double maxAccumulation = rateLimit.getPermits() / 2d;
		return TrickleRateLimiter.perSecondAndAccLimit(perSecond, maxAccumulation);
	}
	
}
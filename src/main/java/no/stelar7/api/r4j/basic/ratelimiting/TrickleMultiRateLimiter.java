package no.stelar7.api.r4j.basic.ratelimiting;


import io.domisum.lib.auxiliumlib.time.ratelimit.TrickleRateLimiter;
import no.stelar7.api.r4j.basic.constants.types.ApiKeyType;

import java.time.Duration;
import java.time.Instant;
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
	public Instant acquire(ApiKeyType keyType, Enum platformOrEndpoint)
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
		return Instant.now().plus(Duration.ofSeconds(30));
	}
	
	@Override
	public void updatePermitsTakenPerX(Map<Integer, Integer> data, ApiKeyType keyTypeUsed, Enum platformOrEndpoint) {}
	
	@Override
	public void resetCalls()
	{
		// not relevant for this implementation
	}
	
	
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
		return new TrickleRateLimiter(rateLimit.getPermits(), rateLimit.getTimeframe(), rateLimit.getPermits() * 0.1);
	}
	
}
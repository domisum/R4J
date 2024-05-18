package no.stelar7.api.r4j.basic.ratelimiting;


import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class TrickleRateLimiter extends RateLimiter
{
	
	// STATE
	private final ReentrantLock lock = new ReentrantLock();
	private final Map<RateLimit, Trickle> trickles = new HashMap<>();
	
	
	// INIT
	public TrickleRateLimiter(List<RateLimit> limits)
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
		return limits.stream()
			.map(l -> trickles.computeIfAbsent(l, Trickle::new))
			.mapToLong(Trickle::getDelayMs)
			.max()
			.orElse(0);
	}
	
	@RequiredArgsConstructor
	private static class Trickle
	{
		
		private final double perSecond;
		private final double maxAccumulation;
		
		private long balanceTime = System.currentTimeMillis();
		private double balance = 0;
		
		
		public Trickle(RateLimit rateLimit)
		{
			this(rateLimit.getPermits() * 1000d / rateLimit.getTimeframeInMS() * 0.95,
				rateLimit.getPermits() / 2d);
		}
		
		public synchronized long getDelayMs()
		{
			long nowMs = System.currentTimeMillis();
			long sinceMs = nowMs - balanceTime;
			
			balanceTime = nowMs;
			balance += sinceMs / 1000d * perSecond;
			balance = Math.min(balance, maxAccumulation);
			
			balance -= 1;
			
			if(balance >= 0)
				return 0;
			long untilBalanceZeroMs = Math.round(balance / perSecond * -1000);
			return untilBalanceZeroMs + 500;
		}
		
	}
	
}
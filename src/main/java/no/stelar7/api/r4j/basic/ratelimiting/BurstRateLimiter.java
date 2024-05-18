package no.stelar7.api.r4j.basic.ratelimiting;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Burst ratelimiter will use as many calls as possible, then wait when it reaches the limit
 */
public class BurstRateLimiter extends RateLimiter
{
	
	private static final Logger logger = LoggerFactory.getLogger(BurstRateLimiter.class);
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public BurstRateLimiter(List<RateLimit> limits)
	{
		super(limits.toArray(new RateLimit[0]));
	}
	
	@Override
	public void acquire()
	{
		lock.lock();
		try
		{
			long sleepTime = getDelay();
			if(sleepTime > 0)
			{
				logger.info("Rate limiting activated! Sleeping for: {}ms", sleepTime);
				Thread.sleep(sleepTime);
			}
		}
		catch(InterruptedException ignored) {}
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	public void updatePermitsTakenPerX(Map<Integer, Integer> data)
	{
		for(var entry : data.entrySet())
			for(var rateLimit : limits)
				if(rateLimit.getTimeframeInMS() / 1000 == entry.getKey())
				{
					long oldVal = callCountInTime.get(rateLimit).get();
					long newVal = entry.getValue();
					if(oldVal + 1 < newVal)
					{
						callCountInTime.get(rateLimit).set(newVal);
						logger.info("Limit {} has changed from {} to {}", entry, oldVal, newVal);
					}
				}
	}
	
	private long getDelay()
	{
		var now = Instant.now();
		
		if(overloadTimer > 0)
			logger.warn("Overload timer set to {}s", overloadTimer);
		long delay = overloadTimer * 1000L;
		overloadTimer = 0;
		
		for(var limit : limits)
		{
			var firstCall = firstCallInTime.computeIfAbsent(limit, (key) -> new AtomicLong(now.toEpochMilli()));
			var callCount = callCountInTime.computeIfAbsent(limit, (key) -> new AtomicLong(0));
			
			long nextIntervalStart = firstCall.get() + limit.getTimeframeInMS();
			long untilNextInterval = nextIntervalStart - now.toEpochMilli();
			if(untilNextInterval <= 0)
			{
				firstCall.set(nextIntervalStart);
				callCount.set(0);
			}
			
			int permits = limit.getPermits();
			permits = (int) Math.floor(permits * 0.95);
			if(untilNextInterval > 0 && callCount.get() >= permits)
			{
				long newDelay = untilNextInterval + 500;
				
				if(newDelay > 10 * 1000)
				{
					logger.warn("Long delay: {} ({})", newDelay, limit);
					newDelay = 10 * 1000;
				}
				
				if(newDelay > delay)
					delay = newDelay;
			}
			
			callCount.incrementAndGet();
			logger.debug("{}: current call count: {}", limit, callCount);
		}
		
		return delay;
	}
	
}
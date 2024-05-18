package no.stelar7.api.r4j.basic.ratelimiting;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Burst ratelimiter will use as many calls as possible, then wait when it reaches the limit
 */
public class BurstRateLimiter extends RateLimiter
{
	
	private static final Logger logger = LoggerFactory.getLogger(BurstRateLimiter.class);
	
	private ReentrantLock lock = new ReentrantLock();
	
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
			update();
			long sleepTime = getDelay();
			if(sleepTime != 0)
			{
				Duration dur = Duration.of(sleepTime, ChronoUnit.MILLIS);
				logger.info("Ratelimited activated! Sleeping for: {}", dur);
			}
			
			Thread.sleep(sleepTime);
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
		for(Entry<Integer, Integer> key : data.entrySet())
		{
			for(RateLimit l : limits)
			{
				if(l.getTimeframeInMS() / 1000 == key.getKey())
				{
					long oldVal = callCountInTime.get(l).get();
					long newVal = key.getValue();
					if(oldVal + 1 < newVal)
					{
						callCountInTime.get(l).set(newVal);
						logger.debug("limit {} has changed from {} to {}", key, oldVal, newVal);
					}
				}
			}
		}
	}
	
	private long getDelay()
	{
		Instant now = Instant.now();
		
		if(overloadTimer > 0)
			logger.warn("Overload timer set to {}", overloadTimer);
		long delay = overloadTimer * 1000L;
		overloadTimer = 0;
		
		for(RateLimit limit : limits)
		{
			long actualCallCount = callCountInTime.get(limit).get();
			if(actualCallCount >= limit.getPermits())
			{
				logger.debug("Calls made in the time frame: {}", actualCallCount);
				logger.debug("Limit for the time frame: {}", limit.getPermits());
				
				long newDelay = firstCallInTime.get(limit).get() + limit.getTimeframeInMS() - now.toEpochMilli();
				newDelay += 500;
				
				if(newDelay > 10 * 1000)
				{
					logger.warn("Long delay: {} ({})", newDelay, limit);
					newDelay = 10 * 1000;
				}
				
				if(newDelay > delay)
					delay = newDelay;
			}
		}
		
		return delay;
	}
	
	private void update()
	{
		Instant now = Instant.now();
		for(RateLimit limit : limits)
		{
			AtomicLong firstCall = firstCallInTime.computeIfAbsent(limit, (key) -> new AtomicLong(0));
			callCountInTime.computeIfAbsent(limit, (key) -> new AtomicLong(0));
			
			long nextIntervalFirstCall = firstCall.get() + limit.getTimeframeInMS();
			long untilNextInterval = nextIntervalFirstCall - now.toEpochMilli();
			if(untilNextInterval <= 0)
			{
				long newFirstCall = -untilNextInterval < 5000 ? nextIntervalFirstCall : now.toEpochMilli();
				firstCallInTime.get(limit).set(newFirstCall);
				callCountInTime.get(limit).set(0);
			}
			
			callCountInTime.get(limit).incrementAndGet();
			
			logger.debug("{}: current call count: {}", limit, callCountInTime.get(limit));
		}
	}
	
}
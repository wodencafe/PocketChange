
package cafe.woden.change;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Coin
{
	DOLLAR_COIN( 100, 0.2 ), HALF_DOLLAR( 50, 0.2 ), QUARTER( 25, 0.4 ), DIME( 10, 0.5 ), NICKEL( 5, 0.5 ), PENNY( 1, 0.8 );

	private int value;

	private double probability;

	private boolean heads;

	private Coin( int value, double probability )
	{
		this.value = value;
		this.probability = probability;
		heads = flipLogic();
	}

	public void flip()
	{
		heads = flipLogic();
	}

	private static boolean flipLogic()
	{
		double flipValue = Math.random();
		if ( flipValue >= 0.5 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Collection<Coin> getTotalValue( int total )
	{
		List<Coin> returnedCoins = new ArrayList<>();
		List<Coin> coins = new ArrayList<>( Arrays.asList( Coin.values() ) );
		int currentTotal = 0;
		while ( currentTotal < total )
		{
			Collections.shuffle( coins );
			Set<Coin> coinsToRemove = new HashSet<>();
			for ( Coin coin : coins )
			{
				if ( coin.value + total >= (currentTotal) )
				{
					coinsToRemove.add( coin );
				}
			}
			for ( Coin coin : coinsToRemove )
			{

				coins.remove( coin );
			}
			Coin coin = get( coins );
			if ( coins.isEmpty() && coin.value + currentTotal > total )
			{
				return returnedCoins;
			}
			currentTotal += coin.value;
			if ( currentTotal > total )
			{
				throw new RuntimeException( "Whoops" );
			}
			returnedCoins.add( coin );
		}
		Collections.shuffle( returnedCoins );
		return returnedCoins;
	}

	public static Set<Coin> getTotalCount( int count )
	{
		Stream<Coin> infiniteCoins = Stream.generate( Coin::get );
		return infiniteCoins.limit( count )
			.collect( Collectors.toSet() );
	}

	public static Coin get( Collection<Coin> coins )
	{
		try
		{
			double p = SecureRandom.getInstanceStrong()
				.doubles( 0.0, 1.0 )
				.findAny()
				.getAsDouble();
			double cumulativeProbability = 0.0;
			for ( Coin coin : coins )
			{
				cumulativeProbability += coin.probability;
				if ( p <= cumulativeProbability )
				{
					return coin;
				}
			}
			return get();

		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}
	}

	public static Coin get()
	{
		try
		{
			double p = SecureRandom.getInstanceStrong()
				.doubles( 0.0, 1.0 )
				.findAny()
				.getAsDouble();
			double cumulativeProbability = 0.0;
			Set<Coin> coins = new HashSet<>( Arrays.asList( Coin.values() ) );
			for ( Coin coin : coins )
			{
				cumulativeProbability += coin.probability;
				if ( p <= cumulativeProbability )
				{
					return coin;
				}
			}
			return get();
		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}
	}

	public int getValue()
	{
		return value;
	}

	public boolean isHeads()
	{
		return heads;
	}
}

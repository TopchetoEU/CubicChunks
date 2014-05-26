/*******************************************************************************
 * Copyright (c) 2014 jeff.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     jeff - initial API and implementation
 ******************************************************************************/
package cuchaz.cubicChunks.util;

import cuchaz.cubicChunks.CubeProvider;
import cuchaz.cubicChunks.world.BlankColumn;
import cuchaz.cubicChunks.world.Column;

public abstract class BlockColumnProcessor extends QueueProcessor
{
	public BlockColumnProcessor( String name, CubeProvider provider, int batchSize )
	{
		super( name, provider, batchSize );
	}
	
	@Override
	public void processBatch( )
	{
		// start processing
		for( long address : m_incomingAddresses )
		{
			// get the block coords
			int blockX = Bits.unpackSigned( address, 26, 0 );
			int blockZ = Bits.unpackSigned( address, 26, 26 );
			
			// get the column
			int cubeX = CubeCoordinate.blockToCube( blockX );
			int cubeZ = CubeCoordinate.blockToCube( blockZ );
			Column column = (Column)m_provider.provideChunk( cubeX, cubeZ );
			
			// skip blank columns
			if( column == null || column instanceof BlankColumn )
			{
				continue;
			}
			
			// get the local coords
			int localX = CubeCoordinate.blockToLocal( blockX );
			int localZ = CubeCoordinate.blockToLocal( blockZ );
			
			// add unsuccessful calculations back onto the queue
			boolean success = calculate( column, localX, localZ, blockX, blockZ );
			if( success )
			{
				m_processedAddresses.add( address );
			}
			else
			{
				m_deferredAddresses.add( address );
			}
		}
	}
	
	public abstract boolean calculate( Column column, int localX, int localZ, int blockX, int blockZ );
}
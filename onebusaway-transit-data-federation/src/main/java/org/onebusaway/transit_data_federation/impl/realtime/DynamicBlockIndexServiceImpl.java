/**
 * Copyright (C) 2023 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.transit_data_federation.impl.realtime;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.transit_data_federation.impl.blocks.BlockIndexFactoryServiceImpl;
import org.onebusaway.transit_data_federation.impl.blocks.BlockStopTimeIndicesFactory;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.blocks.BlockStopTimeIndex;
import org.onebusaway.transit_data_federation.services.blocks.BlockTripIndex;
import org.onebusaway.transit_data_federation.services.blocks.DynamicBlockIndexService;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
/**
 * Counterpart to BlockLocationService, handling dynamic trips
 * ( ADDED / DUPLICATED )
 */
public class DynamicBlockIndexServiceImpl implements DynamicBlockIndexService {

  static final int CACHE_TIMEOUT = 5 * 60 * 1000; // 5 minutes
  @Autowired
  private BlockIndexFactoryServiceImpl blockIndexFactoryService;

  private BlockStopTimeIndicesFactory blockStopTimeIndicesFactory = new BlockStopTimeIndicesFactory();
  // we trivially expire the cache after 5 minutes
  private Map<AgencyAndId, BlockInstance> cacheByBlockId = new PassiveExpiringMap<>(CACHE_TIMEOUT);
  // we trivially expire the cache after 5 minutes
  private Map<AgencyAndId, Set<BlockStopTimeIndex>> blockStopTimeIndicesByStopId = new PassiveExpiringMap<>(CACHE_TIMEOUT);


  @Override
  public List<BlockStopTimeIndex> getStopTimeIndicesForStop(StopEntry stopEntry) {
    if (!blockStopTimeIndicesByStopId.containsKey(stopEntry.getId())) {
      return null;
    }
    Set<BlockStopTimeIndex> set = blockStopTimeIndicesByStopId.get(stopEntry.getId());
    return new ArrayList<>(set);
  }

  @Override
  public void register(BlockInstance blockInstance) {
    // if the vehicle changes trips, we rely on the cache record to expire
    // therefore there may be a brief period of overlap
    AgencyAndId id = blockInstance.getBlock().getBlock().getId();
    cacheByBlockId.put(id, blockInstance);

    List<BlockEntry> blocks = new ArrayList<>();
    blocks.add(blockInstance.getBlock().getBlock());
    List<BlockTripIndex> blockTripIndexList = blockIndexFactoryService.createTripIndices(blocks);

    List<BlockStopTimeIndex> indices = blockStopTimeIndicesFactory.createIndices(blocks);
    for (BlockStopTimeIndex sti : indices) {
      AgencyAndId stopId = sti.getStop().getId();
      if (!blockStopTimeIndicesByStopId.containsKey(stopId)) {
        // a set to prevent duplicates
        blockStopTimeIndicesByStopId.put(stopId, new HashSet<>());
      }
      blockStopTimeIndicesByStopId.get(stopId).add(sti);
    }
  }

  @Override
  public BlockInstance getDynamicBlockInstance(AgencyAndId blockId) {
    return cacheByBlockId.get(blockId);
  }
}

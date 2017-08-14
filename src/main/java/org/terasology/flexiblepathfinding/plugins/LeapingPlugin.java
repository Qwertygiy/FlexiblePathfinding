/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.flexiblepathfinding.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

public class LeapingPlugin extends StandardPlugin {
    public LeapingPlugin(WorldProvider world) {
        super(world);
    }
    private static final Logger logger = LoggerFactory.getLogger(LeapingPlugin.class);

    @Override
    public boolean isReachable(Vector3i to, Vector3i from) {
        // only allowed to move 1 unit in each axis
        if(Math.max(Math.abs(to.x - from.x), Math.max(Math.abs(to.y - from.y), Math.abs(to.z - from.z))) > 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3i occupiedBlock : getOccupiedRegion()) {

            // the start/stop for this block in the occupied region
            Vector3i occupiedBlockTo = new Vector3i(to).add(occupiedBlock);
            Vector3i occupiedBlockFrom = new Vector3i(from).add(occupiedBlock);

            Vector3i toBelow = new Vector3i(occupiedBlockTo).add(Vector3i.down());
            Vector3i fromBelow = new Vector3i(occupiedBlockFrom).add(Vector3i.down());
            Region3i movementBounds = Region3i.createBounded(occupiedBlockTo, occupiedBlockFrom);
            for (Vector3i block : movementBounds) {
                // don't check the blocks below a or b, since those should be solid anyway
                if (block.distanceSquared(toBelow) == 0 || block.distanceSquared(fromBelow) == 0) {
                    continue;
                }

                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }

        return isWalkable(from);
    }

    @Override
    public boolean isWalkable(Vector3i a) {
        Vector3i aBelow = new Vector3i(a).sub(0, 1, 0);
        return world.getBlock(a).isPenetrable() && !world.getBlock(aBelow).isPenetrable();
    }
}

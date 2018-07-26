/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.jdbc.orchestration.reg.newzk.client.zookeeper.section;

import com.google.common.base.Strings;
import io.shardingsphere.jdbc.orchestration.reg.newzk.client.utility.ZookeeperConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

/*
 * Watch event with path data.
 *
 * @author lidongbo
 */
@Slf4j
public class WatchedDataEvent extends WatchedEvent {
    
    @Getter
    private final String data;
    
    public WatchedDataEvent(final WatchedEvent event) {
        this(event, null);
    }
    
    public WatchedDataEvent(final WatchedEvent event, final ZooKeeper zooKeeper) {
        super(event.getType(), event.getState(), event.getPath());
        data = initData(event, zooKeeper);
    }
    
    private String initData(final WatchedEvent event, final ZooKeeper zooKeeper) {
        if (zooKeeper == null) {
            return null;
        }
        if (!zooKeeper.getState().isConnected() && !zooKeeper.getState().isAlive()) {
            log.debug("wait connected..............");
            return null;
        }
        if (Strings.isNullOrEmpty(event.getPath())) {
            return null;
        }
        try {
            if (zooKeeper.exists(event.getPath(), true) != null) {
                return new String(zooKeeper.getData(event.getPath(), true, null), ZookeeperConstants.UTF_8);
            }
        } catch (final KeeperException | InterruptedException ex) {
            // ignore
            log.warn("init data:{}", ex.getMessage());
        }
        return null;
    }
    
    @Override
    public String toString() {
        return super.toString() + " data : " + data;
    }
}

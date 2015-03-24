/*
 * Copyright 2014-2015 CyberVision, Inc.
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

package org.kaaproject.kaa.server.hash;

import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.gen.ConnectionInfo;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsistentHashResolverTest {

    @Test(expected = RuntimeException.class)
    public void getNodeForNullUserIdTest() {
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(new ArrayList<OperationsNodeInfo>(), 5);
        consistentHashResolver.getNode(null);
    }

    @Test
    public void getNodeForEmptyCircleTest() {
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(new ArrayList<OperationsNodeInfo>(), 5);
        OperationsNodeInfo info = consistentHashResolver.getNode("userId");
        Assert.assertNull(info);
    }

    @Test
    public void getNodeForOneItemCircleTest() {
        ConnectionInfo connectionInfo = new ConnectionInfo("thrift1", 4234, ByteBuffer.allocate(16));
        OperationsNodeInfo operationsNodeInfo = new OperationsNodeInfo(connectionInfo, null, 523634L, null);
        List<OperationsNodeInfo> nodes = Arrays.asList(operationsNodeInfo);
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(nodes, 2);
        OperationsNodeInfo returnedNode = consistentHashResolver.getNode("userId");
        Assert.assertEquals(operationsNodeInfo, returnedNode);
    }

    @Test
    public void getNodeForMultipleItemsTest() {
        ByteBuffer buffer1 = ByteBuffer.allocate(8);
        buffer1.put(0, (byte)98);
        buffer1.put(1, (byte)98);
        ByteBuffer buffer2 = ByteBuffer.allocate(8);
        buffer2.put(0, (byte)99);
        buffer2.put(1, (byte)99);
        ByteBuffer buffer3 = ByteBuffer.allocate(2);
        buffer3.put(0, (byte)100);
        buffer3.put(1, (byte)100);
        ConnectionInfo connectionInfo1 = new ConnectionInfo("thrift1", 4241, buffer1);
        ConnectionInfo connectionInfo2 = new ConnectionInfo("thrift2", 4242, buffer2);
        ConnectionInfo connectionInfo3 = new ConnectionInfo("thrift3", 4243, buffer3);
        OperationsNodeInfo operationsNodeInfo1 = new OperationsNodeInfo(connectionInfo1, null, 1231L, null);
        OperationsNodeInfo operationsNodeInfo2 = new OperationsNodeInfo(connectionInfo2, null, 1232L, null);
        OperationsNodeInfo operationsNodeInfo3 = new OperationsNodeInfo(connectionInfo3, null, 1233L, null);
        List<OperationsNodeInfo> nodes = Arrays.asList(operationsNodeInfo1, operationsNodeInfo2, operationsNodeInfo3);
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(nodes, 2);
        OperationsNodeInfo returnedNode = consistentHashResolver.getNode("a");
        // operations node info 1 should be returned
        Assert.assertEquals(operationsNodeInfo1, returnedNode);
    }
}

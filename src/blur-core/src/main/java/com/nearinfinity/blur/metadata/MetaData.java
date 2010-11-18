package com.nearinfinity.blur.metadata;

import java.util.List;
import java.util.Map;

import com.nearinfinity.blur.thrift.BlurAdminServer.NODE_TYPE;
import com.nearinfinity.blur.thrift.generated.BlurException;
import com.nearinfinity.blur.thrift.generated.TableDescriptor;
import com.nearinfinity.mele.Mele;

public interface MetaData {

    List<String> tableList() throws BlurException;

    Map<String, String> shardServerLayout(String table) throws BlurException;

    void registerNode(String hostName, NODE_TYPE type);

    void create(String table, TableDescriptor desc) throws BlurException;

    TableDescriptor describe(String table) throws BlurException;

    void enable(String table) throws BlurException;

    void disable(String table) throws BlurException;

    void drop(String table) throws BlurException;

    List<String> getShardServerHosts();

    List<String> getControllerServerHosts();
    
    Mele getMele();

}
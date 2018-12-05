package com.li.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.li.model.HyRoleModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.storm.shade.org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 把查询数据库的ResultSet 转换为 json结构的树
 * @author peak
 * @Date 2017年2月22日 下午2:35:57
 */
public class ResultSet2JsonTree {


	/**
	 * 把查询数据库的ResultSet 转换为 json结构的树
	 */
	public static JSONArray transform(String topicName,  List<HyRoleModel> models) throws Exception {
		List<NodeInfo> nodes = ResultSet2Nodes(models);
		JSONArray jsonArray = new JSONArray();
		NodeInfo topicNode = new NodeInfo();
		//找到topicName对应的node
		for (NodeInfo nodeInfo : nodes) {
			if( topicName.equals( nodeInfo.getKeyName() ) ){ 
				topicNode = nodeInfo;
				break;
			}
		}
		//循环下     递归解析 node => jsonObject
		for (NodeInfo nodeInfo : nodes) {
			if( topicNode.getId().equals(nodeInfo.getParentId())){
				jsonArray.add( construct(nodeInfo, nodes) );
			}
		}
		return jsonArray;
	}
	/*
	 * 构造 nodeInfo 节点
	 */
	private static JSONObject construct(NodeInfo nodeInfo, List<NodeInfo> nodes){
		JSONObject jObject = (JSONObject) JSONObject.toJSON(nodeInfo);
		JSONArray jArray = new JSONArray();
		
		for (NodeInfo temp : nodes) {
			if( nodeInfo.getId().equals(temp.getParentId())){
				jArray.add(construct(temp, nodes));
			}
		}

		jObject.put("subNodes", jArray);
		return jObject;
	}


	/**
	 * ResultSet 转换为 节点List 
	 */
	private static List<NodeInfo> ResultSet2Nodes( List<HyRoleModel> models) throws Exception {
		List<NodeInfo> nodes = new ArrayList<NodeInfo>();
		if(CollectionUtils.isNotEmpty(models)){
			for(HyRoleModel rs:models){
				NodeInfo nodeInfo = new NodeInfo(
						rs.getId(),
						rs.getParent_id(),
						"0".equals(rs.getIs_key())?false:true,
						rs.getKey_name(),
						JSONObject.parseArray(StringEscapeUtils.unescapeHtml(rs.getFilters())),
						StringEscapeUtils.unescapeHtml(rs.getCalculate()) ,
						rs.getCalculate_mode(),
						rs.getDeal_field());
				nodes.add(nodeInfo);
			}

		}

		return nodes;
	}



	static class NodeInfo {
		private String id;
		private String parentId;
		private boolean isKey;
		private String keyName;
		private JSONArray filters;
		private String calculate;
		private String calculateMode;
		private String dealField;
		private NodeInfo[] subNodes;

		public NodeInfo(){}

		public NodeInfo(String id, String parentId, boolean isKey, String keyName, JSONArray filters, String calculate, String calculateMode, String dealField) {
			super();
			this.id = id;
			this.parentId = parentId;
			this.isKey = isKey;
			this.keyName = keyName;
			this.filters = filters;
			this.calculate = calculate;
			this.calculateMode = calculateMode;
			this.dealField = dealField;
		}

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public boolean getIsKey() {
			return isKey;
		}
		public void setIsKey(boolean isKey) {
			this.isKey = isKey;
		}
		public String getKeyName() {
			return keyName;
		}
		public void setKeyName(String keyName) {
			this.keyName = keyName;
		}
		public JSONArray getFilters() {
			return filters;
		}
		public void setFilters(JSONArray filters) {
			this.filters = filters;
		}
		public String getCalculate() {
			return calculate;
		}
		public void setCalculate(String calculate) {
			this.calculate = calculate;
		}

		public String getCalculateMode() {
			return calculateMode;
		}

		public void setCalculateMode(String calculateMode) {
			this.calculateMode = calculateMode;
		}

		public String getDealField() {
			return dealField;
		}

		public void setDealField(String dealField) {
			this.dealField = dealField;
		}

		public NodeInfo[] getSubNodes() {
			return subNodes;
		}

		public void setSubNodes(NodeInfo[] subNodes) {
			this.subNodes = subNodes;
		}

		@Override
		public String toString() {
			return "NodeInfo [id=" + id + ", parentId=" + parentId + ", isKey=" + isKey + ", keyName=" + keyName
					+ ", filters=" + filters + ", calculate=" + calculate + ", calculateMode=" + calculateMode
					+ ", dealField=" + dealField + ", subNodes=" + Arrays.toString(subNodes) + "]";
		}




	}


}

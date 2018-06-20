package minijava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import symbol.MIdentifier;

/**
 * 该类为图处理的工具类，用于分析继承关系时对继承树进行拓扑排序。
 *
 * @author castor_v_pollux
 * @param <T> 继承{@link MIdentifier} ，方便在检查失败时获得异常信息（如行号、列号等）
 */
public class Graph<T extends MIdentifier> {

	/**
	 * 图的邻接表
	 */
	private HashMap<T, HashSet<T>> graph;

	/**
	 * 图中节点的出度
	 */
	private HashMap<T, Integer> degrees;

	public Graph() {
		graph = new HashMap<>();
		degrees = new HashMap<>();
	}

	/**
	 * 往图中添加新节点
	 * @param node 新节点
	 */
	public void addNode(T node) {
		if (!graph.containsKey(node)) {
			graph.put(node, new HashSet<>());
			degrees.put(node, 0);
		}
	}

	/**
	 * 往图中添加新边
	 * @param fromNode 边的起点
	 * @param toNode 边的终点
	 */
	public void addEdge(T fromNode, T toNode) {
		addNode(fromNode);
		addNode(toNode);
		graph.get(fromNode).add(toNode);
		degrees.replace(toNode, degrees.get(toNode) + 1);
	}

	/**
	 * 对图进行拓扑排序，如果排序失败（存在环）则抛出类继承的异常，到上层进行处理。
	 * @return 若排序成功，返回各节点的一个合法的拓扑顺序
	 * @throws TypeCheckError 类继承的异常
	 */
	public ArrayList<T> topSort() throws TypeCheckError {
		ArrayList<T> ans = new ArrayList<>();
		Queue<T> q = new LinkedList<T>();
		for (Entry<T, Integer> entry : degrees.entrySet())
			if (entry.getValue() == 0)
				q.add(entry.getKey());
		while (!q.isEmpty()) {
			T head = q.remove();
			ans.add(head);
			HashSet<T> next = graph.get(head);
			for (T node : next) {
				int newDegree = degrees.get(node) - 1;
				degrees.replace(node, newDegree);
				if (newDegree == 0)
					q.add(node);
			}
			graph.remove(head);
		}
		if (!graph.isEmpty()) {
			MIdentifier cycleElement = graph.keySet().iterator().next();
			throw new TypeCheckError(String.format("Cycle detected: a cycle exists in the type hierarchy of '%s'",
					cycleElement.getName()), cycleElement.getRow(), cycleElement.getColumn());
		}
		return ans;
	}

}

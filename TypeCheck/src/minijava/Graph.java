package minijava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import symbol.MIdentifier;

/**
 * ����Ϊͼ����Ĺ����࣬���ڷ����̳й�ϵʱ�Լ̳���������������
 *
 * @author castor_v_pollux
 * @param <T> �̳�{@link MIdentifier} �������ڼ��ʧ��ʱ����쳣��Ϣ�����кš��кŵȣ�
 */
public class Graph<T extends MIdentifier> {

	/**
	 * ͼ���ڽӱ�
	 */
	private HashMap<T, HashSet<T>> graph;

	/**
	 * ͼ�нڵ�ĳ���
	 */
	private HashMap<T, Integer> degrees;

	public Graph() {
		graph = new HashMap<>();
		degrees = new HashMap<>();
	}

	/**
	 * ��ͼ������½ڵ�
	 * @param node �½ڵ�
	 */
	public void addNode(T node) {
		if (!graph.containsKey(node)) {
			graph.put(node, new HashSet<>());
			degrees.put(node, 0);
		}
	}

	/**
	 * ��ͼ������±�
	 * @param fromNode �ߵ����
	 * @param toNode �ߵ��յ�
	 */
	public void addEdge(T fromNode, T toNode) {
		addNode(fromNode);
		addNode(toNode);
		graph.get(fromNode).add(toNode);
		degrees.replace(toNode, degrees.get(toNode) + 1);
	}

	/**
	 * ��ͼ�������������������ʧ�ܣ����ڻ������׳���̳е��쳣�����ϲ���д���
	 * @return ������ɹ������ظ��ڵ��һ���Ϸ�������˳��
	 * @throws TypeCheckError ��̳е��쳣
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

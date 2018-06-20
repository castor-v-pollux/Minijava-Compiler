package symbol;

import java.util.HashSet;

/**
 * ��ʾһ��Spiglet��䣬��¼��ǰ������Լ��Ա�����ʹ�á���ֵ�������Ϊ������ͼ�еĽڵ������Է�����
 *
 * @author castor_v_pollux
 */
public class Statement {

	/**
	 * ������ǰ���ͺ��
	 */
	private HashSet<Statement> prev, next;

	/**
	 * ������������ʹ�õı���
	 */
	private HashSet<Integer> def, use;
	/**
	 * �����ִ��ǰ��Ļ�Ծ����
	 */
	public HashSet<Integer> in, out;

	public Statement() {
		prev = new HashSet<>();
		next = new HashSet<>();
		def = new HashSet<>();
		use = new HashSet<>();
		in = new HashSet<>();
		out = new HashSet<>();
	}

	public void addNext(Statement statement) {
		next.add(statement);
		statement.prev.add(this);
	}

	public void addUse(int temp) {
		use.add(temp);
	}

	public void addDef(int temp) {
		def.add(temp);
	}

	/**
	 * ���¸ýڵ�Ļ�Ծ��������
	 * @return �Ƿ��б仯
	 */
	public boolean activityAnalyze() {
		for (Statement next : next)
			out.addAll(next.in);
		HashSet<Integer> newIn = new HashSet<>();
		newIn.addAll(out);
		newIn.removeAll(def);
		newIn.addAll(use);
		if (!in.equals(newIn)) {
			in = newIn;
			return true;
		}
		return false;
	}

}

package symbol;

/**
 * ��ʾһ����������
 *
 * @author castor_v_pollux
 */
public class Interval implements Comparable<Interval> {

	/**
	 * �������俪ʼ�ͽ�����������
	 */
	public int begin, end;
	/**
	 * �û��������Ƿ��Խһ����������
	 */
	public boolean acrossCall;
	/**
	 * �������Ӧ�ı���id
	 */
	public int tempId;

	public Interval(int tempId, int begin, int end) {
		this.begin = begin;
		this.end = end;
		acrossCall = false;
		this.tempId = tempId;
	}

	@Override
	public int compareTo(Interval o) {
		if (begin == o.begin)
			return end - o.end;
		else
			return begin - o.begin;
	}

}

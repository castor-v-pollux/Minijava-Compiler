package symbol;

/**
 * 表示一个活性区间
 *
 * @author castor_v_pollux
 */
public class Interval implements Comparable<Interval> {

	/**
	 * 活性区间开始和结束的语句序号
	 */
	public int begin, end;
	/**
	 * 该活性区间是否跨越一个函数调用
	 */
	public boolean acrossCall;
	/**
	 * 该区间对应的变量id
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

package spiglet;

/**
 * SPiglet的TEMP包装类
 *
 * @author castor_v_pollux
 */
public class STemp extends SCode {

	private static int TempCnt;

	/**
	 * 初始化全局当前寄存器号
	 * @param total 当前已用寄存器
	 */
	public static void init(int total) {
		TempCnt = total;
	}

	public static STemp newTemp() {
		return new STemp(++TempCnt);
	}

	private int id;

	public STemp(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("TEMP %d", id);
	}

}

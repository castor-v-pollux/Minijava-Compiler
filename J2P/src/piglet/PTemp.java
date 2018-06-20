package piglet;

/**
 * <p>piglet代码包装类，继承自{@link PExp}，表示一个piglet临时储存单元TEMP。
 * <p>维护一个全局计数器用来获得新Temp的id。
 * <p>考虑到函数参数均使用TEMP 0~19，故建立一个缓存池，对于0~19号Temp直接从缓存池中获取。
 *
 * @author castor_v_pollux
 */
public class PTemp extends PExp {

	/**
	 * PTemp缓存池
	 */
	private static PTemp[] cache = new PTemp[20];

	static {
		for (int i = 0; i < 20; i++)
			cache[i] = new PTemp(i);
	}

	private static int TempCnt = 20;

	private int id;

	private PTemp(int id) {
		this.id = id;
	}

	/**
	 * 获取一个新的Temp id
	 * @return 一个可用的储存单元号
	 */
	public static int newTempId() {
		return TempCnt++;
	}

	/**
	 * 获取一个新的Temp
	 * @return 一个可用的储存单元
	 */
	public static PTemp newTemp() {
		return new PTemp(TempCnt++);
	}

	/**
	 * 由id获取一个PTemp实例
	 * @param id PTemp序号
	 * @return PTemp实例
	 */
	public static PTemp valueOf(int id) {
		if (id < 20)
			return cache[id];
		else
			return new PTemp(id);
	}

	@Override
	public int print(int depth) {
		String s = String.format("TEMP %d", id);
		System.out.print(s);
		return s.length();
	}

}

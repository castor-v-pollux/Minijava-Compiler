package kanga;

/**
 * <p>该类表示一个Kanga函数，记录其三个参数并进行对栈帧的分配。一个栈帧如下图所示：
 * <ul>
 * <li>Ret addr (4 bytes)
 * <li>Previous frame pointer (4 bytes)
 * <li>Stack used (stack-ReLU(param-4) words)
 * <li>Space for parameters passed to next frame pointer (ReLU(maxcall-4) words)
 * </ul>
 * <p>从上往下，先是4个字节的返回地址，然后是上一个栈帧的开始地址4字节，
 * <p>然后是本过程使用的栈单元，注意到Kanga程序头写的第二个参数中包含了本过程超过四个参数的栈单元，因此要扣除，
 * <p>最后是本过程调用别的过程时，传超过四个参数时用到的栈单元。 * 
 *
 * @author castor_v_pollux
 */
public class Method {

	private int extraParam, stack, maxcall, pos;

	public Method(int param, int stack, int maxcall) {
		this.extraParam = Math.max(param - 4, 0);
		this.stack = stack;
		this.maxcall = maxcall;
		pos = Math.max(maxcall - 4, 0);
	}

	/**
	 * 获取本过程实际的栈帧大小
	 * @return 实际栈帧大小
	 */
	public int getActual() {
		return (2 + stack - extraParam + pos) * 4;
	}

	/**
	 * 将对SpilledReg的访问转为栈地址的访问，如果是本程序的参数，去上一个栈帧找；如果是本程序的栈单元，到中间一段找。
	 * @param id 栈单元id
	 * @return 栈地址
	 */
	public String getSpilledReg(int id) {
		if (id < extraParam)
			return String.format("%d($fp)", id * 4);
		return String.format("%d($sp)", (id - extraParam + pos) * 4);
	}
}

package p2s;

import java.io.InputStream;

import parser.ParseException;
import parser.PigletParser;
import spiglet.STemp;
import syntaxtree.Node;
import visitor.MaxTempVisitor;
import visitor.TranslateVisitor;

/**
 * <p>该类为第三次作业的主类，负责将Piglet代码翻译成SPiglet代码。
 * <p>思路为：由于SPiglet仅仅对Piglet中的嵌套表达式进行了限制，只需要在Piglet每个语句中，
 * 考虑该语句用到的表达式是不是符合SPiglet规范，如果不符合需要新建寄存器使用MOVE语句预处理。
 * <p>故在翻译时，对于语句中使用的表达式，将这里需要的SPiglet表达式类型进行传入，如果具体表达式满足要求，则包装成字符串返回，上层直接打印；
 * 否则在下层预先进行预处理，返回预处理得到的TEMP。
 * 
 * <p>关于SPiglet代码的缩进，由于SPiglet不存在像Piglet那样的复杂嵌套表达式，因此不需要复杂的代码包装，直接输出到控制台即可。
 * 输出时只需要注意对于每个语句，需要在前面缩进一个TAB。
 *
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		// try {
		// in = new FileInputStream(args[0]);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// return;
		// }
		Node goal;
		try {
			goal = new PigletParser(in).Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return;
		}
		STemp.init(goal.accept(new MaxTempVisitor()));
		goal.accept(new TranslateVisitor(), null);
	}

}

package k2m;

import java.io.InputStream;

import parser.KangaParser;
import parser.ParseException;
import syntaxtree.Goal;
import visitor.TranslateVisitor;

/**
 * <p>该类为第五次作业的主类，负责将Kanga代码翻译成MIPS代码。
 * <p>思路为：从Kanga到MIPS的语句几乎是一一对应的，主要任务是对栈的管理。
 * <p>本程序只使用了一个{@link TranslateVisitor}用来翻译。
 * <p>{@link kanga.Method}中详述了一个过程的栈帧设计。
 * 
 * <p>本程序可以将ucla官网上的样例翻译成几乎完全相同的输出，除了最后增加的_error例程以外，唯一的区别在于栈单元用的比它少XD
 *
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		Goal goal;
		try {
			goal = new KangaParser(in).Goal();
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		goal.accept(new TranslateVisitor(), null);
	}

}

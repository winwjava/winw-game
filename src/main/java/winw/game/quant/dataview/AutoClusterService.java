//package winw.game.quant.dataview;
//
//import java.util.List;
//
//import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
//import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
//import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
//import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
//import org.apache.commons.math3.stat.regression.SimpleRegression;
//
//import winw.game.quant.Quote;
//import winw.game.quant.QuoteService;
//
///**
// * 自动聚类。将走势相同的标的聚在一起。
// * 
// * <p>
// * 衡量相关性常用的数学指标为相关性系数，常用的相关性系数有：pearson、spearman、kendall等，Copula函数是一个适用范围极广的处理随机变量之间相关性的统计学工具。
// * 
// * <p>
// * 相关性分析
// * 相关分析是指对两个或多个具备相关性的变量元素进行分析，从而衡量两个因素的的相关密切程度，相关性的元素之间需要存在一定的联系或者概率才可以进行相关性分析。
// * 
// * <p>
// * 相关系数衡量了两个变量的统一程度，范围是-1~1,‘1’代表完全正相关，‘-1’代表完全负相关。比较常用的是Pearson‘皮尔逊’相关系数、Spearman‘斯皮尔曼’相关系数；
// * <li>Pearson相关系数 一般用于分析，两个连续变量之间的关系，是一种线性相关系数； Spearman相关系数
// * <li>Pearson相关系数要求连续变量的取值服从正态分布，不服从正态分布的变量、分类或等级变量之间的关联性可采用Spearman秩相关系数，也称等级相关系数来描述。；
// * <li>P值 P值即概率，反映某一事件发生的可能性大小。统计学根据显著性检验方法所得到的P 值，一般以P < 0.05 为显著， P<0.01
// * 为非常显著，其含义是样本间的差异由抽样误差所致的概率小于0.05 或0.01；
// * <p>
// * 原文链接：https://blog.csdn.net/weixin_50627332/article/details/127193779
// *
// * @see StandardDeviation
// * @see PearsonsCorrelation
// * @see SpearmansCorrelation
// * 
// * @author winw
// *
// *
// */
//public class AutoClusterService {
//
//	public static void main(String[] args) throws Exception {
//
//		QuoteService quoteService = QuoteService.getDefault();
//		List<Quote> list1 = quoteService.get(Quote.class, "sh600519", "2023-07-01", "2023-08-01");// 贵州茅台
//		List<Quote> list2 = quoteService.get(Quote.class, "sz000858", "2023-07-01", "2023-08-01");// 五 粮 液
//		List<Quote> list3 = quoteService.get(Quote.class, "sh601166", "2023-07-01", "2023-08-01");// 兴业银行
//
//		System.out.println(pearsonCorrelation(list1, list2));
//		System.out.println(pearsonCorrelation(list2, list3));
//		System.out.println(pearsonCorrelation(list1, list3));
//	}
//
//	public void cluster() {
//		// 根据全部指标的走势，相同走势的聚为一类。
//		// 时间序列分析。
//		// 相似K线。
//
//		// 价量相似度=0.7价格相似度计算+ 0.3成交量相似度。
//		
//		// 需要做一个预筛选。比如K-Mean聚类。
//		
//		// 先计算10日均线的一个斜率。根据斜率做一个初步筛选。
//		
//		new KMeansPlusPlusClusterer(10);
//		
//	}
//
//	/**
//	 * Pearson Correlation
//	 * 皮尔逊相关系数相关系数是由卡尔.皮尔逊从弗朗西斯.高尔顿在19世纪80年代提出的一个相似但稍有不同的想法演变来的，用于度量两个变量之间的线性相关程度，值介于-1和1之间。两个变量的pearson相关系数用它们的协方差与方差的商表示。
//	 * 
//	 * @return
//	 * 
//	 */
//	public static double pearsonCorrelation(List<Quote> list1, List<Quote> list2) {
//
//		SimpleRegression regression = new SimpleRegression();
//		for (int i = 0; i < list1.size(); i++) {
//			regression.addData(list1.get(i).getClose(), list2.get(i).getClose());
//		}
//
//		return regression.getR();
//
////		double i = 1.1;
////		double[] xArray = new double[] { 31 * 1.1, 31 * 1.1 * 1.1, 31 * 1.1 * 1.1 * 1.1, 31 * 1.1 * 1.1 * 1.1 * 1.1 };
////		double[] yArray = new double[] { 51 * 1.1, 51 * 1.1 * 1.1, 51 * 1.1 * 1.1 * 1.1, 51 * 1.1 * 1.1 * 1.1 * 1.1 };
////
////		PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
////		double c = pearsonsCorrelation.correlation(xArray, yArray);
//	}
//
//	/**
//	 * Spearman相关，又称秩相关、等级相关，是对两变量的秩次大小作线性相关分析，对原始变量的分布不作要求，属于非参数统计方法，适用范围较广。对于服从Pearson相关的数据亦可计算Spearman相关系数，但统计效能更低。
//	 */
//	public void spearmanCorrelation() {
//
//	}
//	
//	// Copula函数
//}

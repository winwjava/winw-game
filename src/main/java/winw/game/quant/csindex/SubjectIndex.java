package winw.game.quant.csindex;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "Subject_Index")
public class SubjectIndex {

//	"index_id":215,
//	"index_code":"000922",
//	"indx_sname":"\u4e2d\u8bc1\u7ea2\u5229",
//	"index_ename":"CSI Dividend",
//	"num":"100",
//	"tclose":"5668.78",
//	"yld_1_mon":"14.30",
//	"base_point":"1000.00",
//	"base_date":"2004-12-31 00:00:00",
//	"online_date":"2008-05-26",
//	"is_custom":0,
//	"is_show":1,
	@Id
	private String index_code;
	@Transient
	private Integer index_id;

	private String indx_sname;
	private String index_ename;
	private Integer num;
	private String tclose;
	
	@Transient
	private String yld_1_mon;

	private Double yieldOneMonth;
	
	private String base_point;
	private String base_date;
	@Transient
	private String online_date;

	@Transient
	private Integer is_custom;
	@Transient
	private Integer is_show;
//	"index_c_fullname":"\u4e2d\u8bc1\u7ea2\u5229\u6307\u6570",
//	"index_e_fullname":"CSI Dividend Index",
//	"class_series":"\u4e2d\u8bc1\u7cfb\u5217\u6307\u6570",
//	"class_eseries":"CSI Indices",
//	"class_region":"\u5883\u5185",
//	"class_eregion":"China Mainland",
//	"class_assets":"\u80a1\u7968",
//	"class_eassets":"Equity",
//	"class_classify":"\u4e3b\u9898",
//	"class_eclassify":"Thematic",
//	"class_currency":"\u4eba\u6c11\u5e01",
//	"class_ecurrency":"CNY",
//	"class_hot":"\u7ea2\u5229\/\u9ad8\u80a1\u606f",
//	"class_ehot":"High Dividend"
	private String index_c_fullname;
	private String index_e_fullname;

	@Transient
	private String class_series;
	@Transient
	private String class_eseries;
	@Transient
	private String class_region;
	@Transient
	private String class_eregion;
	@Transient
	private String class_assets;
	@Transient
	private String class_eassets;
	@Transient
	private String class_classify;
	@Transient
	private String class_eclassify;
	@Transient
	private String class_currency;
	@Transient
	private String class_ecurrency;
	@Transient
	private String class_hot;
	@Transient
	private String class_ehot;

}

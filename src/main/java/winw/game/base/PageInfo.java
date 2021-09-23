package winw.game.base;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = -3928691272412041580L;
    @ApiModelProperty(value = "当前页")
    private int pageNum;
    @ApiModelProperty(value = "每页的数量")
    private int pageSize;
    @ApiModelProperty(value = "总记录数")
    private long total;
    @ApiModelProperty(value = "总页数")
    private int pages;
    @ApiModelProperty(value = "结果集")
    private List<T> list;

    public PageInfo() {
    }

    public PageInfo(org.springframework.data.domain.Page<T> page) {
        this.pageNum = page.getNumber();
        this.pageSize = page.getSize();

        this.pages = page.getTotalPages();
        this.total = page.getTotalElements();

        this.list = page.getContent();
    }

//  public static <T> PageInfo of(org.springframework.data.domain.Page<T> page) {
//      return new PageInfo<T>(page);
//  }

    /**
     * 包装Page对象
     *
     * @param list
     */
//  public PageInfo(List<T> list) {
//      if (list instanceof Page) {
//          Page page = (Page) list;
//          this.pageNum = page.getPageNum();
//          this.pageSize = page.getPageSize();
//
//          this.pages = page.getPages();
//          this.list = page;
//          this.total = page.getTotal();
//      } else if (list instanceof Collection) {
//          this.pageNum = 1;
//          this.pageSize = list.size();
//
//          this.pages = 1;
//          this.list = list;
//          this.total = list.size();
//      }
//      if (list instanceof Collection) {
//          // 判断页面边界
//          judgePageBoudary();
//      }
//  }

    /**
     * 判定页面边界
     */
//  private void judgePageBoudary() {
//      isFirstPage = pageNum == 1;
//      isLastPage = pageNum == pages;
//  }

}

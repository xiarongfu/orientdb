/* Generated By:JJTree: Do not edit this line. OOrderBy.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OOrderBy extends SimpleNode {
  protected List<OOrderByItem> items;

  public OOrderBy() {
    super(-1);
  }

  public OOrderBy(int id) {
    super(id);
  }

  public OOrderBy(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor.
   **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public List<OOrderByItem> getItems() {
    return items;
  }

  public void setItems(List<OOrderByItem> items) {
    this.items = items;
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    if (items != null && items.size() > 0) {
      builder.append("ORDER BY ");
      for (int i = 0; i < items.size(); i++) {
        if (i > 0) {
          builder.append(", ");
        }
        items.get(i).toString(params, builder);
      }
    }
  }

  public int compare(OResult a, OResult b, OCommandContext ctx) {
    for (OOrderByItem item : items) {
      int result = item.compare(a, b, ctx);
      if (result != 0) {
        return result > 0 ? 1 : -1;
      }
    }
    return 0;
  }

  public OOrderBy copy() {
    OOrderBy result = new OOrderBy(-1);
    result.items = items == null ? null : items.stream().map(x -> x.copy()).collect(Collectors.toList());
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OOrderBy oOrderBy = (OOrderBy) o;

    if (items != null ? !items.equals(oOrderBy.items) : oOrderBy.items != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return items != null ? items.hashCode() : 0;
  }

  public void extractSubQueries(SubQueryCollector collector) {
    if (items != null) {
      for (OOrderByItem item : items) {
        item.extractSubQueries(collector);
      }
    }
  }

  public boolean refersToParent() {
    if (items != null) {
      for (OOrderByItem item : items) {
        if (item.refersToParent()) {
          return true;
        }
      }
    }
    return false;
  }

  public OResult serialize() {
    OResultInternal result = new OResultInternal();
    if (items != null) {
      result.setProperty("items", items.stream().map(x -> x.serialize()).collect(Collectors.toList()));
    }
    return result;
  }

  public void deserialize(OResult fromResult) {

    if (fromResult.getProperty("items") != null) {
      List<OResult> ser = fromResult.getProperty("items");
      items = new ArrayList<>();
      for (OResult r : ser) {
        OOrderByItem exp = new OOrderByItem();
        exp.deserialize(r);
        items.add(exp);
      }
    }
  }
}
/* JavaCC - OriginalChecksum=d5529400217169f15e556e5dc6fe4f5b (do not edit this line) */

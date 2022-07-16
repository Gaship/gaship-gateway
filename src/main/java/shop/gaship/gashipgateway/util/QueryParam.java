package shop.gaship.gashipgateway.util;

public class QueryParam {
    String name;
    Object[] values;

    public QueryParam(String name, Object... values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Object[] getValues() {
        return values;
    }
}

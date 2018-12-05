package com.li.model;

import java.io.Serializable;

/**
 * Created by wangdi on 18/6/4.
 */
public class HyRoleModel implements Serializable {

    private static final long serialVersionUID = -8434557005846209841L;

    private String id;
    private String key_name;
    private String parent_id;
    private String calculate_mode;
    private String calculate;
    private String deal_field;
    private String filters;
    private String is_key;
    private String href;
    private String sort;
    private String parent_ids;
    private String create_by;
    private String create_date;
    private String update_by;
    private String update_date;
    private String del_flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey_name() {
        return key_name;
    }

    public void setKey_name(String key_name) {
        this.key_name = key_name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getCalculate_mode() {
        return calculate_mode;
    }

    public void setCalculate_mode(String calculate_mode) {
        this.calculate_mode = calculate_mode;
    }

    public String getCalculate() {
        return calculate;
    }

    public void setCalculate(String calculate) {
        this.calculate = calculate;
    }

    public String getDeal_field() {
        return deal_field;
    }

    public void setDeal_field(String deal_field) {
        this.deal_field = deal_field;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getIs_key() {
        return is_key;
    }

    public void setIs_key(String is_key) {
        this.is_key = is_key;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getParent_ids() {
        return parent_ids;
    }

    public void setParent_ids(String parent_ids) {
        this.parent_ids = parent_ids;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }
}

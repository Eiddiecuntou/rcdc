package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/26
 *
 * @author Jarman
 */
public class CbbUsbInfoRequest implements Request {

    @DispatcherKey
    private String dispatcherKey;

    /**
     * 厂商标识码
     */
    private String idVendor;

    /**
     * 产品标识码
      */
    private String idProduct;

    /**
     * 发行版本号
     */
    private String bcdDevice;

    /**
     * 厂商名
     */
    private String manufacturer;

    /**
     * 产品名
     */
    private String productName;

    /**
     * 终端mac
     */
    private String terminalMac;

    /**
     * 终端名称
     */
    private String terminalName;

    public String getDispatcherKey() {
        return dispatcherKey;
    }

    public void setDispatcherKey(String dispatcherKey) {
        this.dispatcherKey = dispatcherKey;
    }

    public String getIdVendor() {
        return idVendor;
    }

    public void setIdVendor(String idVendor) {
        this.idVendor = idVendor;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getBcdDevice() {
        return bcdDevice;
    }

    public void setBcdDevice(String bcdDevice) {
        this.bcdDevice = bcdDevice;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTerminalMac() {
        return terminalMac;
    }

    public void setTerminalMac(String terminalMac) {
        this.terminalMac = terminalMac;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }
}

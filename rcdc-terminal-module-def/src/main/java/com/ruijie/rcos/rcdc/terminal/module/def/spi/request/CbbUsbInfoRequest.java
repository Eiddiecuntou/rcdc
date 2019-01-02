package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.lang.Nullable;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/26
 *
 * @author Jarman
 */
public class CbbUsbInfoRequest implements Request {

    @NotBlank
    @DispatcherKey
    private String dispatcherKey;

    /**
     * 厂商标识码
     */
    @Nullable
    private String idVendor;

    /**
     * 产品标识码
      */
    @Nullable
    private String idProduct;

    /**
     * 发行版本号
     */
    @Nullable
    private String bcdDevice;

    /**
     * 厂商名
     */
    @Nullable
    private String manufacturer;

    /**
     * 产品名
     */
    @Nullable
    private String productName;

    /**
     * 终端mac
     */
    @Nullable
    private String terminalMac;

    /**
     * 终端名称
     */
    @Nullable
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

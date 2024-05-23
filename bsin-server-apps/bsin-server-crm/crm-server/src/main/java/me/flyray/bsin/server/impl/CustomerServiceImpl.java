package me.flyray.bsin.server.impl;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static me.flyray.bsin.constants.ResponseCode.CUSTOMER_NO_NOT_ISNULL;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.CustomerService;

/**
 * @author bolei
 * @date 2023/6/28 16:38
 * @desc
 */
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }
}

package me.flyray.bsin.common.tenant.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.common.tenant.properties.TenantProperties;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;

import java.util.List;

/**
 * 自定义租户处理器
 *
 * @author sunny
 */
@Slf4j
@AllArgsConstructor
public class CustomeTenantLineHandler implements TenantLineHandler {

    private final TenantProperties tenantProperties;

    @Override
    public String getTenantIdColumn() {
        return StrUtil.isNotBlank(tenantProperties.getColumn()) ? tenantProperties.getColumn() : TenantLineHandler.super.getTenantIdColumn();
    }

    @Override
    public Expression getTenantId() {
        try {
            String tenantId = LoginInfoContextHelper.getTenantId();
            if (StrUtil.isBlank(tenantId)) {
                return new NullValue();
            }
            return new StringValue(tenantId);
        } catch (Exception e) {
            log.error("获取租户ID时发生错误", e);
            return new NullValue();
        }
    }

    @Override
    public boolean ignoreTable(String tableName) {
        try {
            String tenantId = LoginInfoContextHelper.getTenantId();
            if (StrUtil.isBlank(tenantId)) {
                return true;
            }
            List<String> excludeDataSource = tenantProperties.getExcludeDataSource();
            if (ObjectUtil.isNotEmpty(excludeDataSource)) {
                return true;
            }
            List<String> excludeTables = tenantProperties.getExcludeTables();
            return excludeTables.contains(tableName);
        } catch (Exception e) {
            log.error("执行表过滤时发生错误", e);
            return true;
        }
    }
}
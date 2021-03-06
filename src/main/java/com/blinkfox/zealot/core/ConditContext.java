package com.blinkfox.zealot.core;

import java.util.Iterator;
import java.util.Set;
import com.blinkfox.zealot.bean.BuildSource;
import com.blinkfox.zealot.bean.SqlInfo;
import com.blinkfox.zealot.bean.TagHandler;
import com.blinkfox.zealot.config.AbstractZealotConfig;
import com.blinkfox.zealot.log.Log;

/**
 * 构建动态条件查询的上下文协调类
 * Created by blinkfox on 2016/10/30.
 */
public class ConditContext {

    // 得到日志对象
    private static final Log log = Log.get(ConditContext.class);
	
	/**
	 * 私有构造方法
	 */
	private ConditContext() {
		super();
	}

	/**
     * 根据标签名称和对应的构建参数构造出对应标签的sql和参数
     * @param source 构建所需的资源对象
     * @param tag 标签名称
     * @return 返回SqlInfo对象
     */
    protected static SqlInfo buildSqlInfo(BuildSource source, String tag) {
        // 获取所有配置的标签处理对象，并遍历判断当前类型是否符合该type标签
        // 如果符合就执行该标签中对应handler对象的方法
        Set<TagHandler> tagHandlers = AbstractZealotConfig.getTagHandlers();
        for (Iterator<TagHandler> it = tagHandlers.iterator(); it.hasNext();) {
            TagHandler th = it.next();

            // 如果从全局的set中获取到了该标签，则将其前缀和handler对象的方法执行来获取sql和参数
            if (tag.equals(th.getTagName())) {
                source.setPrefix(th.getPrefix());
                try {
                    // 使用反射获取该Handler对应的实例，并执行方法
                    IConditHandler handler = (IConditHandler) th.getHandlerCls().newInstance();
                    return handler.buildSqlInfo(source);
                } catch (InstantiationException e) {
                    log.error("实例化IConditHandler的实现类出错!", e);
                } catch (IllegalAccessException e) {
                    log.error("访问Handler的实现类出错!", e);
                }
            }
        }

        return source.getSqlInfo();
    }

}
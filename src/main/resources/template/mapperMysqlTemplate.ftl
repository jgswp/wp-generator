<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${nameSpace}">
    <resultMap id="BaseResultMap" type="${pack}">
        <id column="${idbeanModel.cloumName}" property="${idbeanModel.javaName}" jdbcType="${idbeanModel.cloumType}"/>
        <#list fieldList as var>
          <result column="${var.cloumName}" property="${var.javaName}" jdbcType="${var.cloumType}"/>
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
         ${idbeanModel.cloumName},<#list fieldList as var> ${var.cloumName},<#if !var_has_next> ${var.cloumName}</#if> </#list>
    </sql>

    <!-- 根据id实现条件查询-->
    <select id="selectByPrimaryKey" resultMap="BaseResultMap" parameterType="java.lang.Integer">
        select
        <include refid="Base_Column_List"/>
        from ${table}
        where ${idbeanModel.cloumName} =  ${r"#{"}${idbeanModel.javaName}${r"}"}
    </select>


    <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
        delete from ${table}  where ${idbeanModel.cloumName} =  ${r"#{"}${idbeanModel.javaName}${r"}"}
    </delete>


    <insert id="insert" parameterType="${pack}">
        <selectKey resultType="${idbeanModel.cloumType}" keyProperty="${idbeanModel.javaName}" order="AFTER">
            SELECT LAST_INSERT_ID()
        </selectKey>
        insert into ${table} ( ${idbeanModel.cloumName},
	    <#list fieldList as var>  ${var.cloumName},
        </#list>)
        values ( ${r"#{"}${idbeanModel.javaName}${r"}"},
        <#list fieldList as var> ${r"#{"}${var.javaName},jdbcType=${var.cloumType}${r"}"},
        </#list> )

    </insert>


    <insert id="insertSelective" parameterType="${pack}">
        <selectKey resultType="${idbeanModel.javaType}" keyProperty="${idbeanModel.javaName}" order="AFTER">
            SELECT LAST_INSERT_ID()
        </selectKey>
        insert into  ${table}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="${idbeanModel.javaName} != null">
            ${idbeanModel.cloumName},
            </if>
	   <#list fieldList as var>
            <if test="${var.javaName} != null">
                ${var.cloumName},
            </if>
       </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="${idbeanModel.javaName} != null">
            ${r"#{"}${idbeanModel.javaName}${r"}"},
            </if>
             <#list fieldList as var>
                 <if test="${var.javaName} != null">
                     ${r"#{"}${var.javaName},jdbcType=${var.cloumType}${r"}"},
                 </if>
             </#list>
        </trim>
    </insert>


    <update id="updateByPrimaryKeySelective" parameterType="${pack}">
        update ${table}
        <set>
            <if test="${idbeanModel.javaName} != null">
            ${id} =  ${r"#{"}${idbeanModel.cloumName}${r"}"},
            </if>
           <#list fieldList as var>
               <if test="${var.javaName} != null">
                   ${var.cloumName} = ${r"#{"}${var.javaName},jdbcType=${var.cloumType}${r"}"},
               </if>
           </#list>
        </set>
        where ${idbeanModel.cloumName} =  ${r"#{"}${idbeanModel.javaName}${r"}"}
    </update>


    <update id="updateByPrimaryKey" parameterType="${pack}">
        update ${table}
        set <#list fieldList as var>
        ${var.cloumName} = ${r"#{"}${var.javaName},jdbcType=${var.cloumType}${r"}"},
    </#list>
        where  ${idbeanModel.cloumName} =  ${r"#{"}${idbeanModel.javaName}${r"}"}
    </update>


    <select id="searchByEntityCount" resultType="java.lang.Integer" parameterType="${pack}">
        select count(*)
        from  ${table}
        <where>
            <if test="${idbeanModel.javaName} != null">
                  ${idbeanModel.cloumName} = ${r"#{"}${idbeanModel.javaName},jdbcType=${idbeanModel.cloumType}${r"}"}
            </if>
           <#list fieldList as var>
               <if test="${var.javaName} != null">
                   AND ${var.cloumName} = ${r"#{"}${var.javaName},jdbcType=${var.cloumType}${r"}"}
               </if>
           </#list>
        </where>
    </select>


    <select id="searchByEntity" resultMap="BaseResultMap" parameterType="${pack}">
        select
        <include refid="Base_Column_List"/>
        from  ${table}
        <where>
            <if test="${idbeanModel.javaName} != null">
            ${idbeanModel.cloumName} = ${r"#{"}${idbeanModel.javaName},jdbcType=${idbeanModel.cloumType}${r"}"}
            </if>
           <#list fieldList as var>
               <if test="${var.javaName} != null">
                   AND    ${var.cloumName} = ${r"#{"}${var.javaName},jdbcType=${var.cloumType}${r"}"}
               </if>
           </#list>
        </where>
    </select>


</mapper>

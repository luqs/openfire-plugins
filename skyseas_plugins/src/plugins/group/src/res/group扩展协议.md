# Group扩展协议
## 摘要
本文定义了一个XMPP扩展协议用于实现持久化的多用户社交圈子功能，即多个XMPP用户可以在同一个圈子中互相交流信息，类似`QQ`的聊天群，用户可以获得自己加入的圈子，创建属于自己的圈子也可以邀请其他用户加入圈子等。

本协议定义了一个简单的圈子控制模型，他保护基本的圈子创建、查询、申请、邀请、踢人等等。

**作者：张智**

## 需求
本文描述了XMPP圈子的最小功能集：

1. 	每个圈子被标示为<groupid@service> (例如：<123@group.jabber.org>)，这里的	`groupid`是圈子的唯一标示而`service`是圈子服务运行的主机名。
2. 	一个用户登录后服务端自动发送出席信息给该用户所加入的每一个圈子。
3. 	一个用户登出后服务端自动发送类型为`unavailable`的出席信息给用户加入的所有圈	子。
4. 	用户可以创建属于自己的圈子，并成为该圈子的所有者。
5. 	允许圈子所有者邀请用户加入自己所有的圈子。
6. 	允许用户申请加入某圈子。
7. 	允许圈子成员退出加入的圈子。
8. 	允许圈子成员发送圈子消息。
9. 	允许圈子成员修改自己的圈子昵称。
10. 允许用户查询圈子服务器中的圈子列表。
11. 允许用户获取自己曾经加入过的圈子列表。
12. 允许圈子所有者修改圈子信息。
13. 允许圈子所有者将圈子用户踢出圈子。
14. 允许圈子所有者解散圈子。

## 术语






## 角色和权限

####以下是已定义的角色：

名称          			| 说明
-----------------------	|------------------------------------
用户	(User)			| 普通的XMPP用户
圈子成员(Member)		| 已经加入圈子的XMPP用户
圈子所有者(Owner)		| 创建圈子的XMPP用户，同事也是圈子成员


####以下是角色相关的权限列表：

权限					| 圈子成员	| 圈子所有者
-----------------------	|------------	|-------------------------
发送圈子消息			| 是			| 是
接收圈子消息			| 是			| 是
邀请用户加入圈子		| 否			| 是
处理用户的加入申请		| 否			| 是
修改自己的圈子昵称		| 是			| 是
修改圈子资料			| 否			| 是
出席消息广播到房间		| 是			| 是
踢人					| 否			| 是
解散圈子				| 否			| 是

## 用户用例

###查询圈子

####例子1.用户提出查询圈子

略

####例子2.服务返回查询表单

略

####例子3.用户提交查询表单

```
<iq from='user@skysea.com' to='group.skysea.com' id='v2' type='set'>
	<action xmlns='http://skysea.com/protocol/group#query'>
		<x xmlns='jabber:x:data' type='submit'>
			<field var='id' type='text-single'>
			 <value></value>
		    </field>
		    <field var='name'  type='text-single'>
		      <value>圈子名称</value>
		    </field>
		    <field var='category' type='list-single'>
		      <value>圈子分类</value>
		    </field>
	  	</x>
	  	<set xmlns='http://jabber.org/protocol/rsm'>
	      <max>10</max>
	      <index>0</index>
	    </set>
  	</action>
</iq>
```
####例子4.服务返回查询出的圈子列表

```
<iq from='group.skysea.com' to='user@skysea.com' id='v2' type='result'>
	<action xmlns='http://skysea.com/protocol/group#query'>
		<x xmlns='jabber:x:data' type='result'>
			<reported>
				<field var='id'/>
				<field var='jid'/>
		        <field var='name'/>
		        <field var='num_members' />
		        <field var='subject'/>
	      	</reported>
			<item>
				<field var='id'> <value>1</value> </field>
				<field var='jid'> <value>group1@group.skysea.com</value> </field>
		        <field var='name'> <value>一起狂欢</value> </field>
		        <field var='num_members'> <value>100</value> </field>
		        <field var='subject'> <value>开心不开心的请跟我来！</value> </field>
		    </item>
		    .
		    [8 more items]
		    .
		    <item>
				<field var='id'> <value>10</value> </field>
				<field var='jid'> <value>group10@group.skysea.com</value> </field>
		        <field var='name'> <value>80后交友</value> </field>
		        <field var='num_members'> <value>70</value> </field>
		        <field var='subject'> <value>80后的伙伴们，一起hi吧！</value> </field>
		    </item>
		</x>
		<set xmlns='http://jabber.org/protocol/rsm'>
	      <first index='0'>1</first>
	      <last>10</last>
	      <count>800</count>
	    </set>
	</action>
</iq>
```


## 圈子所有者用例

###创建圈子

####例子1.用户提出创建申请

略

####例子2.服务返回创建表单

略

####例子3.用户提交创建表单

```
<iq to='group.skysea.com' id='v1' type='set'>
	<action xmlns='http://skysea.com/protocol/group#create'>
		<x xmlns='jabber:x:data' type='submit'>
		    <field var='name'  type='text-single'>
		      <value>圈子名称</value>
		    </field>
		    <field var='category' type='list-single'>
		      <value>1</value>
		    </field>
		    <field var='subject' type='text-single'>
		      <value>圈子主题</value>
		    </field>
		    <field var='description' type='text-multi'>
		      <value>圈子描述</value>
		    </field>
		    <field var='openness' type='list-single'>
		      <value>PUBLIC</value>
		    </field>
	  	</x>
  	</action>
</iq>
```
**圈子分类定义**

值		| 说明
-------	|-----------
1		| 兴趣爱好
2		| 生活休闲
3		| 行业交流


**圈子开放程度定义**

值				| 说明
---------------	|-----------
PUBLIC			| 完全开放
AFFIRM_REQUIRED	| 需要审核

####例子4.服务通知用户圈子创建成功

```
<iq from='group.skysea.com' to='user@jabber.org' id='v1' type='result'>
	<action xmlns="http://skysea.com/protocol/group#create">
		<x xmlns='jabber:x:data' type='result'>
			<field var='jid'>
		      <value>group1@group.jabber.org</value>
		    </field>
		</x>
	</action>
</iq>
```
服务返回表单中`group#id`字段显示了服务为圈子自动生成的`jid`，圈子jid是进行圈子通信功能的目标地址。

###修改圈子信息

####例子1.圈子所有者提出修改申请

略

####例子2.服务返回修改表单

略

####例子3.用户提交修改表单

```
<iq from='user@skysea.com' to='100@group.skysea.com' id='v1' type='set'>
	<action xmlns='http://skysea.com/protocol/group#modify'>
		<x xmlns='jabber:x:data' type='submit'>
		    <field var='name'  type='text-single'>
		      <value>圈子名称</value>
		    </field>
		    <field var='category' type='list-single'>
		      <value>23</value>
		    </field>
		    <field var='subject' type='text-single'>
		      <value>圈子主题</value>
		    </field>
		    <field var='description' type='text-multi'>
		      <value>圈子描述</value>
		    </field>
		    <field var='openness' type='list-single'>
		      <value>PUBLIC</value>
		    </field>
	  	</x>
  	</action>
</iq>
```

####例子4.服务返回修改成功

```
<iq from='100@group.skysea.com' to='user@skysea.com' id='v1' type='result'>
</iq>
```

###删除圈子

####例子1.圈子所有者删除圈子

```
<iq to='100@group.skysea.com' id='v1' type='set'>
	<action xmlns='http://skysea.com/protocol/group#delete'>
  	</action>
</iq>
```

####例子2.服务返回删除成功

```
<iq from='group1@group.skysea.com' to='user@jabber.org' id='v1' type='result'>
</iq>
```
####例子3.服务向圈子成员广播圈子已被删除






## 参考资料

[XEP-0004: Data Forms](http://xmpp.org/extensions/xep-0004.html) 
	([中文](http://wiki.jabbercn.org/XEP-0004))
	
[XEP-0059: Result Set Management](http://xmpp.org/extensions/xep-0059.html) 
	


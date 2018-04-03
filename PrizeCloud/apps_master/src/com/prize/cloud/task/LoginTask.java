/*******************************************
 *版权所有©2015,深圳市铂睿智恒科技有限公司
 *
 *内容摘要：
 *当前版本：1.0.0
 *作	者：yiyi
 *完成日期：2015年7月23日
 *修改记录：
 *修改日期：
 *版 本 号：
 *修 改 人：
 *修改内容：
 ...
 *修改记录：
 *修改日期：
 *版 本 号：
 *修 改 人：
 *修改内容：
 *********************************************/
package com.prize.cloud.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.prize.cloud.R;
import com.prize.cloud.bean.CloudAccount;
import com.prize.cloud.bean.Respond;
import com.prize.cloud.task.pojo.LoginInfo;
import com.prize.cloud.util.CloudIntent;
import com.prize.cloud.util.Utils;

/**
 * 登录
 * 
 * @author yiyi
 * @version 1.0.0
 */
public class LoginTask extends NetTask<String> {
	private String loginName;
	private String password;

	public LoginTask(Context ctx, TaskCallback<String> taskCallback,
			String loginName, String password) {
		super(ctx, taskCallback);
		this.loginName = loginName;
		this.password = password;
	}

	/**
	 * auto activate
	 */
	public LoginTask(Context ctx, TaskCallback<String> callback) {
		super(ctx, callback);
		CloudAccount cloudAccount = null;
		try {
			cloudAccount = dbUtils.findFirst(CloudAccount.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (cloudAccount == null)
			return;
		loginName = cloudAccount.getLoginName();
		password = cloudAccount.getPassword();
	}

	@Override
	public void doExecute() {
		if (password == null || loginName == null) {
			return;
		}
		HttpUtils http = new HttpUtils(8000);
		RequestParams params = new RequestParams();
		params.addHeader("KOOBEE", "dido");
		params.addBodyParameter("username", loginName);
		params.addBodyParameter("password", Utils.getMD5(password));
		String url = HOST + "/cloud/account/login";
		http.send(HttpMethod.POST, url, params, new RespondCallback() {

			@Override
			public void onSuccess(Respond respond) {
				if (respond.getMsg() == null) {
					onTaskError(0, mContext.getResources().getString(R.string.request_failure));
					return;
				}
				if (!respond.getMsg().equals(NETWORK_OK)) {
					if (mContext != null) {
						Toast.makeText(mContext,mContext.getString(R.string.network_connection_fail),
								Toast.LENGTH_SHORT).show();
					}
					return;
				}
				LoginInfo info = respond.convert(LoginInfo.class);
				Log.e("huang", "info="+info.toString());
				if (info == null) {
					return;
				}
				Utils.saveInfo(mContext, info);
				ContentValues values = new ContentValues();
				values.put("loginName", info.getUserId());
				values.put("password", password);
				values.put("passport", info.getPassport());
				Utils.saveAccount(mContext, values);
				onTaskSuccess(info.getPassport());

				Intent it = new Intent(CloudIntent.ACTION_PASSPORT_GET);
				it.putExtra("passport", info.getPassport());
				mContext.sendBroadcast(it);
			}

			@Override
			public void onError(int errorCode, String msg) {
				if (errorCode == NetTask.ERROR_TIMEOUT) {
					onTaskError(errorCode, mContext.getString(R.string.timeout));
				} else if (errorCode == NetTask.ERROR_NETWORK) {
					onTaskError(0, mContext.getResources().getString(R.string.network_connection_fail));
				} else if (errorCode == NetTask.ERROR_FAILURE) {
					onTaskError(errorCode, mContext.getString(R.string.failure));
				} else {
					onTaskError(errorCode, msg);
				}
			}

		});

	}

}

package Test;


import android.util.Log;

import com.woting.common.util.JsonEncloseUtils;

import java.util.ArrayList;

public class TestJson {
	public static void main(String[] args){

		String src=getMessage();
		Log.e("组合消息",""+src);

	}


	private static String getMessage() {
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			TestMessage n = NewMessage(String.valueOf(i));

//			String jsonStr = JsonEncloseUtils.jsonEnclose(n).toString();
//            String jsonStr =String.valueOf(n);
			String jsonStr = JsonEncloseUtils.btToString(n);

			a.add(jsonStr);
		}
//		String str = JsonEncloseUtils.jsonEnclose(a).toString();
//        String str = String.valueOf(a);
		String str = JsonEncloseUtils.jsonEnclose(a).toString();

		return str;
	}

	private static TestMessage NewMessage(String id) {
		TestMessage t = new TestMessage();
		t.setObjId(id);

		ReqParam r = new ReqParam();
		r.setPageSize("10");
		t.setReqParam(r);

		return t;
	}


// 组合消息: ["{\"ObjId\":\"0\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"1\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"2\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"3\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"4\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"5\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"6\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"7\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"8\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"9\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"10\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"11\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"12\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"13\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"14\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"15\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"16\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"17\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"18\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"19\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"20\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"21\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"22\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"23\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"24\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"25\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"26\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"27\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"28\",\"ReqParam\":{\"PageSize\":\"10\"}}","{\"ObjId\":\"29\",\"ReqParam\":{\"PageSize\":\"10\"}}"]

}
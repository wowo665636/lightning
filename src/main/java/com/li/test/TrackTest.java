package com.li.test;

import com.alibaba.fastjson.JSON;
import com.li.common.EttEnum;
import com.li.core.AbstractTrackLog;
import com.li.util.DateUtil;

/**
 * Created by wangdi on 17/12/27.
 */
public class TrackTest extends AbstractTrackLog{
    private static final long serialVersionUID = -8368459414126506870L;

    public static void main(String[] args) {
        TrackTest test = new TrackTest();
        String message ="id=027cec6cd634aa4d8_0_0\tcontainer=1\tappid=news\tnewschn=3\tadslotid=13016\tadslottype=15\tabposition=64\treposition=4\tlc=\trc=\trr=5\tdevice=1\tos=iOS\tosv=11.2.1\tnets=4g\tappv=5.9.9\tsdkv=1.4.0\tpid=1020\tcid=6347835177481711733\tip=124.152.204.142\ttt3=1514359211337\ttt4=\tdelaytrack=0\tappdelaytrack=0\terrorcode=0\tstatus=\tett=\tisspam=\tnewsid=\tsubid=\tssid=\tbssid=\tlontitude=\tlatitude=\tnewsdkpid=\tsubchannelid=\tsuv=\tlocal=\tax=\tay=\tcx=\tcy=\tpgid=\tvchn=\tvid=\tcontentres=\tvc=\tv1code=\tv2code=\tv1=\tv2=\tpoid=\thoursfilt=0\taid=10001062\tcampid=20003284\tadgid=40008110\tcrid=100015293\tpriority=70\tbidtype=3\tcharge=71000\tnongspcharge=71000\tadgres=4\ttemplateid=3\ttemplate=info_pictxt\tbctimetype=0\ttargettype=1\ttargetsetting=1\tfeaid=0\taudienceid=\tturn=2\tgeoid1=1156620100\tgeoid2=1156620100\ttimestamp=1514359211367\tagrossbudget=24600000000\tadaybudget=120000000\tcgrossbudget=99999999900000\tcdaybudget=10000000\tadggrossbudget=99999999900000\tadgdaybudget=10000000";
       // test.testBuild(message);
        //AbstractTrackLog a = new AbstractTrackLog();
        //a.setEtt("na");

        //String ett = a.getEtt();
        DateUtil dateUtil = new DateUtil();
        String time = dateUtil.timestamp2Hour("1516582004606");
        System.out.println(time);



    }

    public String testBuild(String message){
        AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
        System.out.println(JSON.toJSONString(trackLog));
        System.out.println(trackLog.getAdgres());
        if(checkIncome(trackLog)&&"3".equals(trackLog.getBidtype())){
            if("1".equals(trackLog.getAdgres())){
                // 品牌
                System.out.println(1);
            }

            if("2".equals(trackLog.getAdgres())||"3".equals(trackLog.getAdgres())){
                System.out.println(2);
            }


        }
        return "";
    }
}

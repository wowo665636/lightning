package com.li;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangdi on 17/7/28.
 */
public class MainTest {
    public static void main(String[] args) {
        String old_str = "{'aid=10000884-campid=20001178-adgid=40002097-crid=100003940': '8000', 'aid=10001263-campid=20001435-adgid=40003306-crid=100006030': '64800', 'aid=10001263-campid=20001435-adgid=40003144-crid=100005654': '26400', 'aid=10000764-campid=20001332-adgid=40002471-crid=100004583': '31500', 'aid=10000111-campid=20002643-adgid=40006516-crid=100012131': '101000', 'aid=10000111-campid=20002643-adgid=40006522-crid=100012191': '290410', 'aid=10000111-campid=20002643-adgid=40006516-crid=100012133': '92000', 'aid=10000586-campid=20002735-adgid=40006810-crid=100012669': '150000', 'aid=10001043-campid=20001128-adgid=40002081-crid=100003916': '7000', 'aid=10000586-campid=20002733-adgid=40006801-crid=100012658': '75000', 'aid=10000586-campid=20002778-adgid=40006985-crid=100012944': '75000', 'aid=10000111-campid=20002576-adgid=40006338-crid=100011724': '1107000', 'aid=10000141-campid=20002435-adgid=40005594-crid=100010910': '1050', 'aid=10001062-campid=20001819-adgid=40003563-crid=100008367': '303000', 'aid=10000764-campid=20002017-adgid=40004136-crid=100007543': '37800', 'aid=10001966-campid=20002327-adgid=40005308-crid=100010423': '429600', 'aid=10000586-campid=20002776-adgid=40006971-crid=100012920': '75000', 'aid=10001951-campid=20002268-adgid=40006425-crid=100011914': '42000', 'aid=10000586-campid=20002733-adgid=40006805-crid=100012665': '225000', 'aid=10000111-campid=20002576-adgid=40006337-crid=100011721': '253216', 'aid=10000141-campid=20002288-adgid=40005090-crid=100009993': '3150', 'aid=10000111-campid=20002576-adgid=40006337-crid=100011722': '86717', 'aid=10001043-campid=20002640-adgid=40006874-crid=100012758': '28000', 'aid=10001951-campid=20002268-adgid=40005302-crid=100010406': '2100', 'aid=10000884-campid=20001178-adgid=40002096-crid=100003938': '4000', 'aid=10000883-campid=20002646-adgid=40006527-crid=100012202': '920230', 'aid=10000813-campid=20000770-adgid=40001215-crid=100002287': '10500', 'aid=10000883-campid=20002646-adgid=40006527-crid=100012200': '964241', 'aid=10000883-campid=20002646-adgid=40006527-crid=100012201': '1000249', 'aid=10001923-campid=20002182-adgid=40004764-crid=100009185': '33300', 'aid=10000586-campid=20002793-adgid=40007018-crid=100013010': '75000', 'aid=10000141-campid=20002460-adgid=40005714-crid=100011043': '1050', 'aid=10000811-campid=20001310-adgid=40003463-crid=100006395': '4000', 'aid=10001043-campid=20002640-adgid=40006933-crid=100012868': '72000', 'aid=10000111-campid=20002643-adgid=40006522-crid=100012190': '194801', 'aid=10000111-campid=20002582-adgid=40006344-crid=100011754': '590083', 'aid=10001951-campid=20002268-adgid=40006452-crid=100011963': '33600', 'aid=10000111-campid=20002582-adgid=40006344-crid=100011751': '176000', 'aid=10001263-campid=20001435-adgid=40002826-crid=100005136': '37200', 'aid=10000811-campid=20001866-adgid=40003643-crid=100006705': '4000', 'aid=10000141-campid=20002461-adgid=40005716-crid=100011045': '1050', 'aid=10000586-campid=20002735-adgid=40006808-crid=100012668': '72680', 'aid=10000111-campid=20002643-adgid=40006522-crid=100012192': '113299', 'aid=10000586-campid=20002735-adgid=40006807-crid=100012667': '454486', 'aid=10001043-campid=20002640-adgid=40006771-crid=100012619': '100000', 'aid=10002152-campid=20002773-adgid=40007034-crid=100013034': '4200', 'aid=10001597-campid=20002043-adgid=40004140-crid=100007569': '225225', 'aid=10001062-campid=20001247-adgid=40002303-crid=100004331': '202000', 'aid=10002146-campid=20002746-adgid=40006859-crid=100012726': '60000', 'aid=10001779-campid=20002420-adgid=40005571-crid=100010886': '9450', 'aid=10001989-campid=20002411-adgid=40005573-crid=100010887': '178500', 'aid=10001809-campid=20001948-adgid=40003870-crid=100007055': '126000', 'aid=10000586-campid=20002731-adgid=40006797-crid=100012644': '207733', 'aid=10002045-campid=20002606-adgid=40006408-crid=100011872': '15407000', 'aid=10001484-campid=20001563-adgid=40003519-crid=100006490': '3234', 'aid=10000586-campid=20002796-adgid=40007027-crid=100013025': '150000', 'aid=10000883-campid=20002644-adgid=40006526-crid=100012199': '1163900', 'aid=10000141-campid=20002557-adgid=40006270-crid=100011623': '5250', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013018': '929016', 'aid=10001544-campid=20002443-adgid=40005623-crid=100010946': '4885301', 'aid=10001855-campid=20002027-adgid=40004138-crid=100007567': '1000', 'aid=10000111-campid=20002576-adgid=40006337-crid=100011719': '170899', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013011': '920414', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013012': '894608', 'aid=10001989-campid=20002411-adgid=40005550-crid=100010837': '196000', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013014': '851197', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013015': '868802', 'aid=10000764-campid=20001325-adgid=40002467-crid=100004579': '77700', 'aid=10000586-campid=20002687-adgid=40006639-crid=100012417': '75000', 'aid=10000764-campid=20001204-adgid=40002208-crid=100004319': '37800', 'aid=10001965-campid=20002430-adgid=40005587-crid=100010900': '3300', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011740': '1794429', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011741': '2343240', 'aid=10001062-campid=20001247-adgid=40002303-crid=100009425': '202000', 'aid=10000586-campid=20002688-adgid=40006643-crid=100012421': '75000', 'aid=10001062-campid=20001819-adgid=40003563-crid=100006581': '101000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011774': '1274000', 'aid=10001951-campid=20002268-adgid=40005035-crid=100009885': '12600', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011776': '1727000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011777': '817492', 'aid=10000111-campid=20002576-adgid=40006338-crid=100011726': '50000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011773': '637000', 'aid=10000141-campid=20000649-adgid=40000924-crid=100001800': '840', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011750': '1266782', 'aid=10002146-campid=20002746-adgid=40006860-crid=100012727': '170000', 'aid=10001746-campid=20002220-adgid=40004867-crid=100009440': '72000', 'aid=10000586-campid=20002791-adgid=40007013-crid=100013006': '225000', 'aid=10001597-campid=20001767-adgid=40003455-crid=100006378': '26950', 'aid=10001989-campid=20002411-adgid=40005946-crid=100011282': '409500', 'aid=10000586-campid=20002561-adgid=40006282-crid=100011640': '145000', 'aid=10000586-campid=20002819-adgid=40007082-crid=100013116': '75000', 'aid=10002152-campid=20002773-adgid=40007037-crid=100013036': '12600', 'aid=10000811-campid=20000769-adgid=40001214-crid=100002288': '20000', 'aid=10000811-campid=20001310-adgid=40004694-crid=100009021': '4000', 'aid=10001263-campid=20001435-adgid=40002992-crid=100005391': '34800', 'aid=10000586-campid=20002793-adgid=40007017-crid=100013009': '155000', 'aid=10000586-campid=20002807-adgid=40007055-crid=100013079': '77065', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013070': '9900', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013071': '7920', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013072': '7590', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013073': '6600', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013074': '5280', 'aid=10001746-campid=20002220-adgid=40004908-crid=100009643': '36000', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011745': '1545806', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011744': '1274000', 'aid=10001062-campid=20001247-adgid=40002305-crid=100009426': '100682', 'aid=10001989-campid=20002411-adgid=40005574-crid=100010888': '140000', 'aid=10001043-campid=20001128-adgid=40002033-crid=100003870': '7000', 'aid=10001965-campid=20002342-adgid=40005347-crid=100010469': '2970', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011743': '1365000', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011742': '2002000', 'aid=10000764-campid=20002279-adgid=40005064-crid=100009944': '2138640', 'aid=10002056-campid=20002652-adgid=40006532-crid=100012208': '100000', 'aid=10000813-campid=20000770-adgid=40001453-crid=100002790': '10500', 'aid=10000111-campid=20002755-adgid=40007012-crid=100013003': '204000', 'aid=10000586-campid=20002562-adgid=40006291-crid=100011649': '60000', 'aid=10001453-campid=20002533-adgid=40006181-crid=100011522': '1204200', 'aid=10000141-campid=20002416-adgid=40005562-crid=100010866': '1540', 'aid=10001894-campid=20002120-adgid=40004486-crid=100008447': '15000', 'aid=10000586-campid=20002731-adgid=40006799-crid=100012653': '147824', 'aid=10000813-campid=20000770-adgid=40001267-crid=100002355': '31500', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013013': '903210', 'aid=10000141-campid=20002290-adgid=40005092-crid=100009998': '2100', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011747': '176000', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011746': '263675', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011748': '506553', 'aid=10000586-campid=20002699-adgid=40006661-crid=100012449': '227037', 'aid=10000884-campid=20001178-adgid=40002098-crid=100003941': '8000', 'aid=10001949-campid=20002262-adgid=40004973-crid=100009788': '187600', 'aid=10000586-campid=20002722-adgid=40006764-crid=100012612': '150000', 'aid=10000586-campid=20002797-adgid=40007030-crid=100013029': '75000', 'aid=10000141-campid=20002585-adgid=40006351-crid=100011780': '2100', 'aid=10000586-campid=20002718-adgid=40006743-crid=100012591': '75000', 'aid=10001536-campid=20001620-adgid=40003041-crid=100005555': '2200', 'aid=10001958-campid=20002351-adgid=40005362-crid=100012445': '11550', 'aid=10001989-campid=20002411-adgid=40005945-crid=100011281': '210000', 'aid=10001951-campid=20002268-adgid=40006480-crid=100012025': '77700', 'aid=10000702-campid=20001929-adgid=40003839-crid=100006999': '1133292', 'aid=10000586-campid=20002795-adgid=40007023-crid=100013021': '150000', 'aid=10002148-campid=20002802-adgid=40007049-crid=100013065': '330', 'aid=10000586-campid=20002687-adgid=40006638-crid=100012416': '156000', 'aid=10000141-campid=20002599-adgid=40006392-crid=100011839': '2100', 'aid=10000586-campid=20002730-adgid=40006794-crid=100012640': '1448450', 'aid=10001536-campid=20001620-adgid=40003087-crid=100005609': '2800', 'aid=10000697-campid=20002769-adgid=40006952-crid=100012902': '14520', 'aid=10001484-campid=20001563-adgid=40003779-crid=100006931': '3234', 'aid=10000811-campid=20000768-adgid=40001211-crid=100002284': '4000', 'aid=10001965-campid=20002346-adgid=40005354-crid=100010474': '660', 'aid=10000811-campid=20000768-adgid=40001213-crid=100002286': '32000', 'aid=10001965-campid=20002339-adgid=40005339-crid=100010467': '5280', 'aid=10000586-campid=20002667-adgid=40006566-crid=100012281': '390000', 'aid=10000586-campid=20002669-adgid=40006609-crid=100012377': '466000', 'aid=10000586-campid=20002711-adgid=40006718-crid=100012558': '449042', 'aid=10001746-campid=20001906-adgid=40003769-crid=100006924': '8400', 'aid=10000586-campid=20002689-adgid=40006647-crid=100012425': '442940', 'aid=10001746-campid=20002220-adgid=40004868-crid=100009441': '12000', 'aid=10001967-campid=20002617-adgid=40006710-crid=100012537': '15700', 'aid=10001746-campid=20002220-adgid=40004866-crid=100009439': '16000', 'aid=10001263-campid=20001435-adgid=40002626-crid=100004803': '12000', 'aid=10001762-campid=20001925-adgid=40003832-crid=100006991': '1200', 'aid=10000764-campid=20000696-adgid=40001051-crid=100002263': '510300', 'aid=10001779-campid=20002295-adgid=40005107-crid=100010017': '4725', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011737': '2399848', 'aid=10000884-campid=20000813-adgid=40001323-crid=100002458': '12000', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011739': '1425028', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011738': '1955074', 'aid=10001964-campid=20002392-adgid=40005511-crid=100010872': '20130', 'aid=10001043-campid=20001128-adgid=40002169-crid=100004076': '8400', 'aid=10001951-campid=20002268-adgid=40005050-crid=100009935': '12600', 'aid=10000111-campid=20002755-adgid=40006930-crid=100012862': '102000', 'aid=10001951-campid=20002268-adgid=40005538-crid=100010817': '25200', 'aid=10000586-campid=20002709-adgid=40006702-crid=100012530': '156000', 'aid=10001484-campid=20002264-adgid=40005006-crid=100009836': '9702', 'aid=10001544-campid=20002443-adgid=40005626-crid=100010947': '2140461', 'aid=10000811-campid=20001310-adgid=40003462-crid=100006394': '4000', 'aid=10001043-campid=20002640-adgid=40007043-crid=100013043': '100784000', 'aid=10001062-campid=20001247-adgid=40002305-crid=100004333': '196691', 'aid=10000883-campid=20002644-adgid=40006525-crid=100012195': '384000', 'aid=10001918-campid=20002215-adgid=40004975-crid=100009791': '1400', 'aid=10001263-campid=20001435-adgid=40002623-crid=100004802': '3600', 'aid=10001079-campid=20001196-adgid=40002196-crid=100004117': '16800', 'aid=10001043-campid=20002640-adgid=40006570-crid=100012285': '4000', 'aid=10000697-campid=20002769-adgid=40006949-crid=100012899': '2165955', 'aid=10001779-campid=20002221-adgid=40004870-crid=100009443': '4725', 'aid=10000141-campid=20001902-adgid=40003754-crid=100008177': '4900', 'aid=10000141-campid=20002366-adgid=40005411-crid=100010566': '5250', 'aid=10000586-campid=20002778-adgid=40006984-crid=100012943': '150000', 'aid=10001043-campid=20001128-adgid=40002231-crid=100004175': '10500', 'aid=10001043-campid=20001128-adgid=40002227-crid=100004171': '6300', 'aid=10001762-campid=20001925-adgid=40003983-crid=100007271': '1200', 'aid=10000586-campid=20002763-adgid=40006927-crid=100012856': '150000', 'aid=10000586-campid=20002739-adgid=40006829-crid=100012689': '278101', 'aid=10001615-campid=20001799-adgid=40003466-crid=100006404': '8000', 'aid=10000764-campid=20001717-adgid=40003259-crid=100006626': '583800', 'aid=10000141-campid=20002390-adgid=40005508-crid=100010738': '2100', 'aid=10001062-campid=20001247-adgid=40002301-crid=100009424': '96469', 'aid=10000141-campid=20002231-adgid=40004892-crid=100009610': '9450', 'aid=10001043-campid=20002640-adgid=40006508-crid=100012123': '21000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011775': '1183000', 'aid=10001597-campid=20001767-adgid=40003359-crid=100006333': '30800', 'aid=10001809-campid=20001948-adgid=40004176-crid=100007651': '97650', 'aid=10000811-campid=20001310-adgid=40004695-crid=100009022': '4000', 'aid=10000586-campid=20002780-adgid=40006996-crid=100012958': '308000', 'aid=10000586-campid=20002777-adgid=40006978-crid=100012934': '540000', 'aid=10000586-campid=20002669-adgid=40006608-crid=100012376': '75000', 'aid=10000764-campid=20002279-adgid=40005064-crid=100011972': '1248240', 'aid=10000586-campid=20002777-adgid=40006979-crid=100012935': '75000', 'aid=10001043-campid=20002640-adgid=40006933-crid=100012867': '302000', 'aid=10000141-campid=20002461-adgid=40005717-crid=100011046': '1050', 'aid=10001062-campid=20001247-adgid=40002306-crid=100009427': '80775', 'aid=10000111-campid=20002584-adgid=40006350-crid=100011778': '3536158', 'aid=10000111-campid=20002584-adgid=40006350-crid=100011779': '3179883', 'aid=10000141-campid=20000381-adgid=40000801-crid=100001568': '840', 'aid=10000141-campid=20002535-adgid=40006183-crid=100011525': '1050', 'aid=10000141-campid=20001867-adgid=40003642-crid=100008179': '4900', 'aid=10001965-campid=20002347-adgid=40005356-crid=100010482': '330', 'aid=10000586-campid=20002561-adgid=40006284-crid=100011642': '462000', 'aid=10001746-campid=20001906-adgid=40003906-crid=100007107': '21000', 'aid=10000141-campid=20001653-adgid=40003091-crid=100005560': '31500', 'aid=10001762-campid=20001925-adgid=40003991-crid=100007283': '14400', 'aid=10000111-campid=20002755-adgid=40006930-crid=100012863': '100485', 'aid=10000111-campid=20002582-adgid=40006344-crid=100011752': '173247', 'aid=10000764-campid=20001715-adgid=40003258-crid=100006402': '693000', 'aid=10000141-campid=20002343-adgid=40005348-crid=100010470': '394240', 'aid=10000586-campid=20002794-adgid=40007021-crid=100013019': '150000', 'aid=10000883-campid=20002644-adgid=40006525-crid=100012196': '388000', 'aid=10000141-campid=20002417-adgid=40005565-crid=100010870': '2100', 'aid=10000883-campid=20002644-adgid=40006525-crid=100012194': '392000', 'aid=10000764-campid=20001717-adgid=40003259-crid=100008180': '331800', 'aid=10001043-campid=20002640-adgid=40006946-crid=100012896': '294000', 'aid=10001043-campid=20002640-adgid=40006946-crid=100012895': '1294000', 'aid=10000141-campid=20002431-adgid=40005588-crid=100010903': '4620', 'aid=10001809-campid=20002107-adgid=40004451-crid=100008349': '37800', 'aid=10001053-campid=20002205-adgid=40004839-crid=100009451': '17901', 'aid=10001746-campid=20001906-adgid=40003810-crid=100006970': '8400', 'aid=10000586-campid=20002797-adgid=40007031-crid=100013030': '75000', 'aid=10001343-campid=20002560-adgid=40006278-crid=100011634': '512460', 'aid=10000141-campid=20002232-adgid=40004895-crid=100009612': '287700', 'aid=10002148-campid=20002802-adgid=40007048-crid=100013060': '330', 'aid=10001536-campid=20001620-adgid=40003121-crid=100005616': '6800', 'aid=10000586-campid=20002567-adgid=40006316-crid=100011705': '78000', 'aid=10001343-campid=20002560-adgid=40006278-crid=100011632': '494780', 'aid=10001343-campid=20002560-adgid=40006278-crid=100011633': '495820'}";

        String new_str="{'aid=10000884-campid=20001178-adgid=40002097-crid=100003940': '8000', 'aid=10001263-campid=20001435-adgid=40003306-crid=100006030': '64800', 'aid=10001263-campid=20001435-adgid=40003144-crid=100005654': '26400', 'aid=10000764-campid=20001332-adgid=40002471-crid=100004583': '31500', 'aid=10000111-campid=20002643-adgid=40006516-crid=100012131': '101000', 'aid=10000111-campid=20002643-adgid=40006522-crid=100012191': '290410', 'aid=10000111-campid=20002643-adgid=40006516-crid=100012133': '92000', 'aid=10000586-campid=20002735-adgid=40006810-crid=100012669': '150000', 'aid=10001043-campid=20001128-adgid=40002081-crid=100003916': '7000', 'aid=10000586-campid=20002733-adgid=40006801-crid=100012658': '75000', 'aid=10000586-campid=20002778-adgid=40006985-crid=100012944': '75000', 'aid=10000111-campid=20002576-adgid=40006338-crid=100011724': '1107000', 'aid=10000141-campid=20002435-adgid=40005594-crid=100010910': '1050', 'aid=10001062-campid=20001819-adgid=40003563-crid=100008367': '303000', 'aid=10000764-campid=20002017-adgid=40004136-crid=100007543': '37800', 'aid=10001966-campid=20002327-adgid=40005308-crid=100010423': '429600', 'aid=10000586-campid=20002776-adgid=40006971-crid=100012920': '75000', 'aid=10001951-campid=20002268-adgid=40006425-crid=100011914': '42000', 'aid=10000586-campid=20002733-adgid=40006805-crid=100012665': '225000', 'aid=10000111-campid=20002576-adgid=40006337-crid=100011721': '253216', 'aid=10000141-campid=20002288-adgid=40005090-crid=100009993': '3150', 'aid=10000111-campid=20002576-adgid=40006337-crid=100011722': '86717', 'aid=10001043-campid=20002640-adgid=40006874-crid=100012758': '28000', 'aid=10001951-campid=20002268-adgid=40005302-crid=100010406': '2100', 'aid=10000884-campid=20001178-adgid=40002096-crid=100003938': '4000', 'aid=10000883-campid=20002646-adgid=40006527-crid=100012202': '920230', 'aid=10000813-campid=20000770-adgid=40001215-crid=100002287': '10500', 'aid=10000883-campid=20002646-adgid=40006527-crid=100012200': '964241', 'aid=10000883-campid=20002646-adgid=40006527-crid=100012201': '1000249', 'aid=10001923-campid=20002182-adgid=40004764-crid=100009185': '33300', 'aid=10000586-campid=20002793-adgid=40007018-crid=100013010': '75000', 'aid=10000141-campid=20002460-adgid=40005714-crid=100011043': '1050', 'aid=10000811-campid=20001310-adgid=40003463-crid=100006395': '4000', 'aid=10001043-campid=20002640-adgid=40006933-crid=100012868': '72000', 'aid=10000111-campid=20002643-adgid=40006522-crid=100012190': '194801', 'aid=10000111-campid=20002582-adgid=40006344-crid=100011754': '590083', 'aid=10001951-campid=20002268-adgid=40006452-crid=100011963': '33600', 'aid=10000111-campid=20002582-adgid=40006344-crid=100011751': '176000', 'aid=10001263-campid=20001435-adgid=40002826-crid=100005136': '37200', 'aid=10000811-campid=20001866-adgid=40003643-crid=100006705': '4000', 'aid=10000141-campid=20002461-adgid=40005716-crid=100011045': '1050', 'aid=10000586-campid=20002735-adgid=40006808-crid=100012668': '72680', 'aid=10000111-campid=20002643-adgid=40006522-crid=100012192': '113299', 'aid=10000586-campid=20002735-adgid=40006807-crid=100012667': '454486', 'aid=10001043-campid=20002640-adgid=40006771-crid=100012619': '100000', 'aid=10002152-campid=20002773-adgid=40007034-crid=100013034': '4200', 'aid=10001597-campid=20002043-adgid=40004140-crid=100007569': '225225', 'aid=10001062-campid=20001247-adgid=40002303-crid=100004331': '202000', 'aid=10002146-campid=20002746-adgid=40006859-crid=100012726': '60000', 'aid=10001779-campid=20002420-adgid=40005571-crid=100010886': '9450', 'aid=10001989-campid=20002411-adgid=40005573-crid=100010887': '178500', 'aid=10001809-campid=20001948-adgid=40003870-crid=100007055': '126000', 'aid=10000586-campid=20002731-adgid=40006797-crid=100012644': '207733', 'aid=10002045-campid=20002606-adgid=40006408-crid=100011872': '15400000', 'aid=10001484-campid=20001563-adgid=40003519-crid=100006490': '3234', 'aid=10000586-campid=20002796-adgid=40007027-crid=100013025': '150000', 'aid=10000883-campid=20002644-adgid=40006526-crid=100012199': '1163900', 'aid=10000141-campid=20002557-adgid=40006270-crid=100011623': '5250', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013018': '929016', 'aid=10001544-campid=20002443-adgid=40005623-crid=100010946': '4885301', 'aid=10001855-campid=20002027-adgid=40004138-crid=100007567': '1000', 'aid=10000111-campid=20002576-adgid=40006337-crid=100011719': '170899', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013011': '920414', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013012': '894608', 'aid=10001989-campid=20002411-adgid=40005550-crid=100010837': '196000', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013014': '851197', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013015': '868802', 'aid=10000764-campid=20001325-adgid=40002467-crid=100004579': '77700', 'aid=10000586-campid=20002687-adgid=40006639-crid=100012417': '75000', 'aid=10000764-campid=20001204-adgid=40002208-crid=100004319': '37800', 'aid=10001965-campid=20002430-adgid=40005587-crid=100010900': '3300', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011740': '1794429', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011741': '2343240', 'aid=10001062-campid=20001247-adgid=40002303-crid=100009425': '202000', 'aid=10000586-campid=20002688-adgid=40006643-crid=100012421': '75000', 'aid=10001062-campid=20001819-adgid=40003563-crid=100006581': '101000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011774': '1274000', 'aid=10001951-campid=20002268-adgid=40005035-crid=100009885': '12600', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011776': '1727000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011777': '817492', 'aid=10000111-campid=20002576-adgid=40006338-crid=100011726': '50000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011773': '637000', 'aid=10000141-campid=20000649-adgid=40000924-crid=100001800': '840', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011750': '1266782', 'aid=10002146-campid=20002746-adgid=40006860-crid=100012727': '170000', 'aid=10001746-campid=20002220-adgid=40004867-crid=100009440': '72000', 'aid=10000586-campid=20002791-adgid=40007013-crid=100013006': '225000', 'aid=10001597-campid=20001767-adgid=40003455-crid=100006378': '26950', 'aid=10001989-campid=20002411-adgid=40005946-crid=100011282': '409500', 'aid=10000586-campid=20002561-adgid=40006282-crid=100011640': '145000', 'aid=10000586-campid=20002819-adgid=40007082-crid=100013116': '75000', 'aid=10002152-campid=20002773-adgid=40007037-crid=100013036': '12600', 'aid=10000811-campid=20000769-adgid=40001214-crid=100002288': '20000', 'aid=10000811-campid=20001310-adgid=40004694-crid=100009021': '4000', 'aid=10001263-campid=20001435-adgid=40002992-crid=100005391': '34800', 'aid=10000586-campid=20002793-adgid=40007017-crid=100013009': '155000', 'aid=10000586-campid=20002807-adgid=40007055-crid=100013079': '77065', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013070': '9900', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013071': '7920', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013072': '7590', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013073': '6600', 'aid=10002148-campid=20002802-adgid=40007050-crid=100013074': '5280', 'aid=10001746-campid=20002220-adgid=40004908-crid=100009643': '36000', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011745': '1545806', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011744': '1274000', 'aid=10001062-campid=20001247-adgid=40002305-crid=100009426': '100682', 'aid=10001989-campid=20002411-adgid=40005574-crid=100010888': '140000', 'aid=10001043-campid=20001128-adgid=40002033-crid=100003870': '7000', 'aid=10001965-campid=20002342-adgid=40005347-crid=100010469': '2970', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011743': '1365000', 'aid=10000111-campid=20002581-adgid=40006342-crid=100011742': '2002000', 'aid=10000764-campid=20002279-adgid=40005064-crid=100009944': '2138640', 'aid=10002056-campid=20002652-adgid=40006532-crid=100012208': '100000', 'aid=10000813-campid=20000770-adgid=40001453-crid=100002790': '10500', 'aid=10000111-campid=20002755-adgid=40007012-crid=100013003': '204000', 'aid=10000586-campid=20002562-adgid=40006291-crid=100011649': '60000', 'aid=10001453-campid=20002533-adgid=40006181-crid=100011522': '1204200', 'aid=10000141-campid=20002416-adgid=40005562-crid=100010866': '1540', 'aid=10001894-campid=20002120-adgid=40004486-crid=100008447': '15000', 'aid=10000586-campid=20002731-adgid=40006799-crid=100012653': '147824', 'aid=10000813-campid=20000770-adgid=40001267-crid=100002355': '31500', 'aid=10000883-campid=20002792-adgid=40007016-crid=100013013': '903210', 'aid=10000141-campid=20002290-adgid=40005092-crid=100009998': '2100', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011747': '176000', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011746': '263675', 'aid=10000111-campid=20002582-adgid=40006343-crid=100011748': '506553', 'aid=10000586-campid=20002699-adgid=40006661-crid=100012449': '227037', 'aid=10000884-campid=20001178-adgid=40002098-crid=100003941': '8000', 'aid=10001949-campid=20002262-adgid=40004973-crid=100009788': '187600', 'aid=10000586-campid=20002722-adgid=40006764-crid=100012612': '150000', 'aid=10000586-campid=20002797-adgid=40007030-crid=100013029': '75000', 'aid=10000141-campid=20002585-adgid=40006351-crid=100011780': '2100', 'aid=10000586-campid=20002718-adgid=40006743-crid=100012591': '75000', 'aid=10001536-campid=20001620-adgid=40003041-crid=100005555': '2200', 'aid=10001958-campid=20002351-adgid=40005362-crid=100012445': '11550', 'aid=10001989-campid=20002411-adgid=40005945-crid=100011281': '210000', 'aid=10001951-campid=20002268-adgid=40006480-crid=100012025': '77700', 'aid=10000702-campid=20001929-adgid=40003839-crid=100006999': '1133292', 'aid=10000586-campid=20002795-adgid=40007023-crid=100013021': '150000', 'aid=10002148-campid=20002802-adgid=40007049-crid=100013065': '330', 'aid=10000586-campid=20002687-adgid=40006638-crid=100012416': '156000', 'aid=10000141-campid=20002599-adgid=40006392-crid=100011839': '2100', 'aid=10000586-campid=20002730-adgid=40006794-crid=100012640': '1448450', 'aid=10001536-campid=20001620-adgid=40003087-crid=100005609': '2800', 'aid=10000697-campid=20002769-adgid=40006952-crid=100012902': '14520', 'aid=10001484-campid=20001563-adgid=40003779-crid=100006931': '3234', 'aid=10000811-campid=20000768-adgid=40001211-crid=100002284': '4000', 'aid=10001965-campid=20002346-adgid=40005354-crid=100010474': '660', 'aid=10000811-campid=20000768-adgid=40001213-crid=100002286': '32000', 'aid=10001965-campid=20002339-adgid=40005339-crid=100010467': '5280', 'aid=10000586-campid=20002667-adgid=40006566-crid=100012281': '390000', 'aid=10000586-campid=20002669-adgid=40006609-crid=100012377': '466000', 'aid=10000586-campid=20002711-adgid=40006718-crid=100012558': '449042', 'aid=10001746-campid=20001906-adgid=40003769-crid=100006924': '8400', 'aid=10000586-campid=20002689-adgid=40006647-crid=100012425': '442940', 'aid=10001746-campid=20002220-adgid=40004868-crid=100009441': '12000', 'aid=10001967-campid=20002617-adgid=40006710-crid=100012537': '15700', 'aid=10001746-campid=20002220-adgid=40004866-crid=100009439': '16000', 'aid=10001263-campid=20001435-adgid=40002626-crid=100004803': '12000', 'aid=10001762-campid=20001925-adgid=40003832-crid=100006991': '1200', 'aid=10000764-campid=20000696-adgid=40001051-crid=100002263': '510300', 'aid=10001779-campid=20002295-adgid=40005107-crid=100010017': '4725', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011737': '2399848', 'aid=10000884-campid=20000813-adgid=40001323-crid=100002458': '12000', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011739': '1425028', 'aid=10000111-campid=20002581-adgid=40006341-crid=100011738': '1955074', 'aid=10001964-campid=20002392-adgid=40005511-crid=100010872': '20130', 'aid=10001043-campid=20001128-adgid=40002169-crid=100004076': '8400', 'aid=10001951-campid=20002268-adgid=40005050-crid=100009935': '12600', 'aid=10000111-campid=20002755-adgid=40006930-crid=100012862': '102000', 'aid=10001951-campid=20002268-adgid=40005538-crid=100010817': '25200', 'aid=10000586-campid=20002709-adgid=40006702-crid=100012530': '156000', 'aid=10001484-campid=20002264-adgid=40005006-crid=100009836': '9702', 'aid=10001544-campid=20002443-adgid=40005626-crid=100010947': '2140461', 'aid=10000811-campid=20001310-adgid=40003462-crid=100006394': '4000', 'aid=10001043-campid=20002640-adgid=40007043-crid=100013043': '100784000', 'aid=10001062-campid=20001247-adgid=40002305-crid=100004333': '196691', 'aid=10000883-campid=20002644-adgid=40006525-crid=100012195': '384000', 'aid=10001918-campid=20002215-adgid=40004975-crid=100009791': '1400', 'aid=10001263-campid=20001435-adgid=40002623-crid=100004802': '3600', 'aid=10001079-campid=20001196-adgid=40002196-crid=100004117': '16800', 'aid=10001043-campid=20002640-adgid=40006570-crid=100012285': '4000', 'aid=10000697-campid=20002769-adgid=40006949-crid=100012899': '2165955', 'aid=10001779-campid=20002221-adgid=40004870-crid=100009443': '4725', 'aid=10000141-campid=20001902-adgid=40003754-crid=100008177': '4900', 'aid=10000141-campid=20002366-adgid=40005411-crid=100010566': '5250', 'aid=10000586-campid=20002778-adgid=40006984-crid=100012943': '150000', 'aid=10001043-campid=20001128-adgid=40002231-crid=100004175': '10500', 'aid=10001043-campid=20001128-adgid=40002227-crid=100004171': '6300', 'aid=10001762-campid=20001925-adgid=40003983-crid=100007271': '1200', 'aid=10000586-campid=20002763-adgid=40006927-crid=100012856': '150000', 'aid=10000586-campid=20002739-adgid=40006829-crid=100012689': '278101', 'aid=10001615-campid=20001799-adgid=40003466-crid=100006404': '8000', 'aid=10000764-campid=20001717-adgid=40003259-crid=100006626': '583800', 'aid=10000141-campid=20002390-adgid=40005508-crid=100010738': '2100', 'aid=10001062-campid=20001247-adgid=40002301-crid=100009424': '96469', 'aid=10000141-campid=20002231-adgid=40004892-crid=100009610': '9450', 'aid=10001043-campid=20002640-adgid=40006508-crid=100012123': '21000', 'aid=10000111-campid=20002584-adgid=40006349-crid=100011775': '1183000', 'aid=10001597-campid=20001767-adgid=40003359-crid=100006333': '30800', 'aid=10001809-campid=20001948-adgid=40004176-crid=100007651': '97650', 'aid=10000811-campid=20001310-adgid=40004695-crid=100009022': '4000', 'aid=10000586-campid=20002780-adgid=40006996-crid=100012958': '308000', 'aid=10000586-campid=20002777-adgid=40006978-crid=100012934': '540000', 'aid=10000586-campid=20002669-adgid=40006608-crid=100012376': '75000', 'aid=10000764-campid=20002279-adgid=40005064-crid=100011972': '1248240', 'aid=10000586-campid=20002777-adgid=40006979-crid=100012935': '75000', 'aid=10001043-campid=20002640-adgid=40006933-crid=100012867': '302000', 'aid=10000141-campid=20002461-adgid=40005717-crid=100011046': '1050', 'aid=10001062-campid=20001247-adgid=40002306-crid=100009427': '80775', 'aid=10000111-campid=20002584-adgid=40006350-crid=100011778': '3536158', 'aid=10000111-campid=20002584-adgid=40006350-crid=100011779': '3179883', 'aid=10000141-campid=20000381-adgid=40000801-crid=100001568': '840', 'aid=10000141-campid=20002535-adgid=40006183-crid=100011525': '1050', 'aid=10000141-campid=20001867-adgid=40003642-crid=100008179': '4900', 'aid=10001965-campid=20002347-adgid=40005356-crid=100010482': '330', 'aid=10000586-campid=20002561-adgid=40006284-crid=100011642': '462000', 'aid=10001746-campid=20001906-adgid=40003906-crid=100007107': '21000', 'aid=10000141-campid=20001653-adgid=40003091-crid=100005560': '31500', 'aid=10001762-campid=20001925-adgid=40003991-crid=100007283': '14400', 'aid=10000111-campid=20002755-adgid=40006930-crid=100012863': '100485', 'aid=10000111-campid=20002582-adgid=40006344-crid=100011752': '173247', 'aid=10000764-campid=20001715-adgid=40003258-crid=100006402': '693000', 'aid=10000141-campid=20002343-adgid=40005348-crid=100010470': '394240', 'aid=10000586-campid=20002794-adgid=40007021-crid=100013019': '150000', 'aid=10000883-campid=20002644-adgid=40006525-crid=100012196': '388000', 'aid=10000141-campid=20002417-adgid=40005565-crid=100010870': '2100', 'aid=10000883-campid=20002644-adgid=40006525-crid=100012194': '392000', 'aid=10000764-campid=20001717-adgid=40003259-crid=100008180': '331800', 'aid=10001043-campid=20002640-adgid=40006946-crid=100012896': '294000', 'aid=10001043-campid=20002640-adgid=40006946-crid=100012895': '1294000', 'aid=10000141-campid=20002431-adgid=40005588-crid=100010903': '4620', 'aid=10001809-campid=20002107-adgid=40004451-crid=100008349': '37800', 'aid=10001053-campid=20002205-adgid=40004839-crid=100009451': '17901', 'aid=10001746-campid=20001906-adgid=40003810-crid=100006970': '8400', 'aid=10000586-campid=20002797-adgid=40007031-crid=100013030': '75000', 'aid=10001343-campid=20002560-adgid=40006278-crid=100011634': '512460', 'aid=10000141-campid=20002232-adgid=40004895-crid=100009612': '287700', 'aid=10002148-campid=20002802-adgid=40007048-crid=100013060': '330', 'aid=10001536-campid=20001620-adgid=40003121-crid=100005616': '6800', 'aid=10000586-campid=20002567-adgid=40006316-crid=100011705': '78000', 'aid=10001343-campid=20002560-adgid=40006278-crid=100011632': '494780', 'aid=10001343-campid=20002560-adgid=40006278-crid=100011633': '495820'}" ;
        Map<String, String> map1 = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 降序排序
                        return obj2.compareTo(obj1);
                    }
                });
        Map<String, String> map2 = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 降序排序
                        return obj2.compareTo(obj1);
                    }
                });
        Map<String,String> old_map =  (Map<String,String>)JSONObject.parse(old_str);
        Map<String,String> new_map =  (Map<String,String>)JSONObject.parse(new_str);
        map1 = old_map;
        map2 = new_map;
       // System.out.println(map1.equals(map2));

        System.out.println(JSON.toJSONString(map1));

        System.out.println("------------------------------------");

        System.out.println(JSON.toJSONString(map2));


    }
}

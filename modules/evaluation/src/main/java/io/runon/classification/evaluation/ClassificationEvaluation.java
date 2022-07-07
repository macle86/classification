package io.runon.classification.evaluation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 분류평가
 * @author macle
 */
public class ClassificationEvaluation {
//    P : 전체 참인 수
//    N : 전체 거짓인 수
//    TP (True Positive) : 참을 참이라고 한 횟수
//    TN (True Negative) : 거짓을 거짓이라고 한 횟수
//    FN (False Negative) : 참을 거짓이라고 한 횟수
//    FP (False Positive) : 거짓을 참이라고 한 횟수

    String id;
    String name;

    long tp;
    long tn;
    long fn;
    long fp;

    int scale = 4;

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public ClassificationEvaluation(){
        init();
    }

    public ClassificationEvaluation(long tp, long tn, long fn, long fp){
        this.tp = tp;
        this.tn = tn;
        this.fn = fn;
        this.fp = fp;
    }


    public void init(){

        tp = 0;
        tn = 0;
        fn = 0;
        fp = 0;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTruePositive(long tp){
        this.tp = tp;
    }


    public void setTrueNegative(long tn){
        this.tn = tn;
    }

    public void setFalseNegative(long fn){
        this.fn = fn;
    }

    public void setFalsePositive(long fp){
        this.fp = fp;
    }

    public long getTruePositive(){
        return tp;
    }
    public long getTrueNegative(){
        return tn;
    }
    public long getFalseNegative(){
        return fn;
    }
    public long getFalsePositive(){
        return fp;
    }

    public void addTruePositive(){
        tp++;
    }

    public void addTrueNegative(){
        tn++;
    }

    public void addFalseNegative(){
        fn++;
    }

    public void addFalsePositive(){
        fp++;
    }

    public void addTruePositive(long tp){
        this.tp += tp;
    }

    public void addTrueNegative(long tn){
        this.tn += tn;
    }

    public void addFalseNegative(long fn){
        this.fn += fn;
    }

    public void addFalsePositive(long fp){
        this.fp += fp;
    }

    public void add(ClassificationEvaluation evaluation){
        this.tp += evaluation.tp;
        this.tn += evaluation.tn;
        this.fn += evaluation.fn;
        this.fp += evaluation.fp;
    }

    /**
     * Accuracy : 정확도, 제대로 분류된 데이터의 비Error Rate : 오류율, 잘못 분류한 데이터의 비율
     * @return 0 ~ 1
     */
    public BigDecimal accuracy(){
        return new BigDecimal(tp+tn).divide(new BigDecimal(tp+tn+fp+fn),scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * Geometric Mean : 균형 정확도
     * 참에 대한 정확도와, 거짓에 대한 정확도를 따로 분류해 기하 평균을 구함
     * @return 0 ~ 1
     */
    public BigDecimal geometricMean(){

        if(tp ==0 || tn == 0){
            return BigDecimal.ZERO;
        }

        BigDecimal tpfn = new BigDecimal(tp).divide(new BigDecimal(tp+fn), MathContext.DECIMAL128);
        BigDecimal tnfp = new BigDecimal(tn).divide(new BigDecimal(fp+tn), MathContext.DECIMAL128);

        //BigDecimal sqrt 가없는 java 8용 한국시장 남풉형 프로젝트에서는 아직 java 8 이 많이 사용됨
        //noinspection UnpredictableBigDecimalConstructorCall
        return new BigDecimal(Math.sqrt(tpfn.multiply(tnfp).doubleValue())).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public BigDecimal f1Score(){

        if(tp == 0){
            return BigDecimal.ZERO;
        }

        BigDecimal precision = new BigDecimal(tp).divide(new BigDecimal(tp+fp), MathContext.DECIMAL128);
        BigDecimal recall = new BigDecimal(tp).divide(new BigDecimal(tp+fn),scale, RoundingMode.HALF_UP);

        if(recall.compareTo(BigDecimal.ZERO) ==0){
            return BigDecimal.ZERO;
        }

        BigDecimal up = precision.multiply(recall);
        BigDecimal down = precision.add(recall);

        return new BigDecimal(2).multiply(up).divide(down, scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * Precision : 정밀도, 예측한 정답 중 실제 정답인 것
     */
    public BigDecimal precision(){
        if(tp == 0){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(tp).divide(new BigDecimal(tp+fp),scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     *  재현율, 실제 정답 중 예측에 성공한 것
     *  통계학 sensitivity
     *
     */
    public BigDecimal recall(){
        if(tp == 0){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(tp).divide(new BigDecimal(tp+fn),scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * Error Rate : 오류율, 잘못 분류한 데이터의 비율
     * @return 0 ~ 1
     */
    public BigDecimal errorRate(){
        return new BigDecimal(fn+ fp).divide(new BigDecimal(tp+tn+fp+fn),scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public long getPositive(){
        return tp + tn;
    }

    public long getNegative(){
        return fp + fn;
    }

    public long length(){
        return tp + tn + fp + fn;
    }

    @Override
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(toJsonObject());
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();
        if(id != null){
            jsonObject.addProperty("id",id);
        }
        if(name != null){
            jsonObject.addProperty("name", name);
        }
        if(length() == 0){
            return jsonObject;
        }

        setJsonObject(jsonObject);
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject ){

        jsonObject.addProperty("length", length());
        jsonObject.addProperty("accuracy", accuracy());
        jsonObject.addProperty("f1_score", f1Score());
        jsonObject.addProperty("geometric_mean", geometricMean());
        jsonObject.addProperty("precision", precision());
        jsonObject.addProperty("recall", recall());

        jsonObject.addProperty("p", getPositive());
        jsonObject.addProperty("n", getNegative());
        jsonObject.addProperty("tp", tp);
        jsonObject.addProperty("tn", tn);
        jsonObject.addProperty("fn", fn);
        jsonObject.addProperty("fp", fp);
    }
}

package io.runon.classification.evaluation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 다항분류 평가
 * @author macle
 */
public class MultinomialEvaluation {

    String id;
    String name;

    int scale = 4;

    public int getScale() {
        return scale;
    }


    long p = 0;
    long n = 0;

    ClassificationEvaluation [] evaluations;
    public MultinomialEvaluation(ClassificationEvaluation [] evaluations){
        this.evaluations = evaluations;
    }

    private Map<String, ClassificationEvaluation> map = null;

    /**
     * 맵 구조 생성
     * 모든 데이터에 아이디가 설정되어 있다는 전제가 있어야 한다
     */
    public void makeMap(){
        if(map != null){
            return;
        }
        map = new HashMap<>();
        for(ClassificationEvaluation evaluation : evaluations){
            map.put(evaluation.getId(), evaluation);
        }

    }

    public ClassificationEvaluation getEvaluation(String id){
        if(map == null){
            makeMap();
        }
        return map.get(id);
    }

    public ClassificationEvaluation[] getEvaluations() {
        return evaluations;
    }



    /**
     * 정보추가
     * @param classificationId 분류아이디
     * @param trueId 정답 아이디
     */
    public void add(String classificationId, String trueId ){
//    TP (True Positive) : 참을 참이라고 한 횟수
//    TN (True Negative) : 거짓을 거짓이라고 한 횟수
//    FN (False Negative) : 참을 거짓이라고 한 횟수
//    FP (False Positive) : 거짓을 참이라고 한 횟수
        if(classificationId.equals(trueId)){
            p++;
            //정답일때
            for(ClassificationEvaluation evaluation : evaluations){
                if(evaluation.getId().equals(trueId)){
                    evaluation.addTruePositive();
                }else{
                    evaluation.addTrueNegative();
                }
            }
        }else{
            n++;
            for(ClassificationEvaluation evaluation : evaluations){
                if(evaluation.getId().equals(trueId)){
                    evaluation.addFalseNegative();
                }else if(evaluation.getId().equals(classificationId)){
                    evaluation.addFalsePositive();
                }else{
                    evaluation.addTrueNegative();
                }
            }
        }
    }

    public long length(){
        return p+n;
    }

    public long getPositive(){
        return p;
    }

    public long getNegative(){
        return n;
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

    public BigDecimal accuracy(){


        BigDecimal sum = BigDecimal.ZERO;
        for(ClassificationEvaluation evaluation :evaluations){
            sum = sum.add(evaluation.accuracy());
        }
        return sum.divide(new BigDecimal(evaluations.length), scale,RoundingMode.HALF_UP);

    }

    public BigDecimal f1Score(){
        
        //다시 작성하기
        BigDecimal precisionSum = BigDecimal.ZERO;
        BigDecimal recallSum = BigDecimal.ZERO;

        for(ClassificationEvaluation evaluation :evaluations){
            precisionSum = precisionSum.add(new BigDecimal(evaluation.tp).divide(new BigDecimal(evaluation.tp+evaluation.fp), MathContext.DECIMAL128));
            recallSum = recallSum.add(new BigDecimal(evaluation.tp).divide(new BigDecimal(evaluation.tp+evaluation.fn),scale, RoundingMode.HALF_UP));
        }
        BigDecimal length = new BigDecimal(evaluations.length);

        //평균
        BigDecimal precision = precisionSum.divide(length, MathContext.DECIMAL128);
        BigDecimal recall = recallSum.divide(length, MathContext.DECIMAL128);

        BigDecimal up = precision.multiply(recall);
        BigDecimal down = precision.add(recall);

        return new BigDecimal(2).multiply(up).divide(down, scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }


    public BigDecimal geometricMean(){
    
        BigDecimal sum = BigDecimal.ZERO;
        for(ClassificationEvaluation evaluation :evaluations){
            sum = sum.add(evaluation.geometricMean());
        }
        return sum.divide(new BigDecimal(evaluations.length), scale,RoundingMode.HALF_UP);
        
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
        jsonObject.addProperty("accuracy", accuracy());
        jsonObject.addProperty("f1_score", f1Score());
        jsonObject.addProperty("geometric_mean", geometricMean());
        jsonObject.addProperty("p", getPositive());
        jsonObject.addProperty("n", getNegative());

        JsonArray array = new JsonArray();
        for(ClassificationEvaluation evaluation :evaluations){
            array.add(evaluation.toJsonObject());
        }

        jsonObject.add("evaluations", array);

        return jsonObject;
    }

}

package io.runon.classification.evaluation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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

    public void setScale(int scale) {
        this.scale = scale;

        for(ClassificationEvaluation evaluation : evaluations){
            evaluation.setScale(scale);
        }
    }

    long p = 0;
    long n = 0;

    ClassificationEvaluation [] evaluations;
    public MultinomialEvaluation(ClassificationEvaluation [] evaluations){
        this.evaluations = evaluations;
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

        int count = 0;

        BigDecimal sum = BigDecimal.ZERO;
        for(ClassificationEvaluation evaluation :evaluations){
            if(evaluation.length() == 0){
                continue;
            }
            count++;
            sum = sum.add(evaluation.accuracy());
        }

        if(count == 0){
            return BigDecimal.ZERO;
        }

        return sum.divide(new BigDecimal(count), scale,RoundingMode.HALF_UP);

    }

    public BigDecimal f1Score(){
        int count = 0;
        BigDecimal precisionSum = BigDecimal.ZERO;
        BigDecimal recallSum = BigDecimal.ZERO;

        for(ClassificationEvaluation evaluation :evaluations){
            if(evaluation.length() ==0){
                continue;
            }

            count++;

            if(evaluation.tp == 0){
                continue;
            }

            precisionSum = precisionSum.add(new BigDecimal(evaluation.tp).divide(new BigDecimal(evaluation.tp+evaluation.fp), MathContext.DECIMAL128));
            recallSum = recallSum.add(new BigDecimal(evaluation.tp).divide(new BigDecimal(evaluation.tp+evaluation.fn), MathContext.DECIMAL128));
        }

        if(recallSum.compareTo(BigDecimal.ZERO) == 0 ){
            return BigDecimal.ZERO;
        }

        BigDecimal length = new BigDecimal(count);

        //평균
        BigDecimal precision = precisionSum.divide(length, MathContext.DECIMAL128);
        BigDecimal recall = recallSum.divide(length, MathContext.DECIMAL128);

        BigDecimal up = precision.multiply(recall);
        BigDecimal down = precision.add(recall);

        return new BigDecimal(2).multiply(up).divide(down, scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }


    public BigDecimal geometricMean(){

        int count = 0;

        BigDecimal sum = BigDecimal.ZERO;
        for(ClassificationEvaluation evaluation :evaluations){
            if(evaluation.length() ==0){
                continue;
            }

            count++;
            sum = sum.add(evaluation.geometricMean());
        }
        if(count == 0){
            return BigDecimal.ZERO;
        }
        return sum.divide(new BigDecimal(count), scale,RoundingMode.HALF_UP);
        
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
        if(length()  == 0){
            return jsonObject;
        }

        setJsonObject(jsonObject);

        JsonArray array = new JsonArray();
        for(ClassificationEvaluation evaluation :evaluations){
            if(evaluation.length() > 0){
                array.add(evaluation.toJsonObject());
            }

        }

        jsonObject.add("evaluations", array);

        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject ){
        jsonObject.addProperty("length", length());
        jsonObject.addProperty("simple_accuracy", new BigDecimal(getPositive()).divide(new BigDecimal(length()),scale, RoundingMode.HALF_UP).stripTrailingZeros());
        jsonObject.addProperty("accuracy", accuracy());
        jsonObject.addProperty("f1_score", f1Score());
        jsonObject.addProperty("geometric_mean", geometricMean());
        jsonObject.addProperty("p", getPositive());
        jsonObject.addProperty("n", getNegative());
    }
}

package io.runon.classification.evaluation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 다항분류 평가
 * @author macle
 */
public class MultinomialEvaluation {

    public enum Mode{
        SUM
        , AVERAGE
    }

    String id;
    String name;

    int scale = 4;

    public int getScale() {
        return scale;
    }

    ClassificationEvaluation [] evaluations;
    public MultinomialEvaluation(ClassificationEvaluation [] evaluations){
        this.evaluations = evaluations;
    }
    public MultinomialEvaluation(){

    }

    public void setEvaluations(ClassificationEvaluation[] evaluations) {
        this.evaluations = evaluations;
    }

    public ClassificationEvaluation[] getEvaluations() {
        return evaluations;
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

    public long getTruePositive(){
        long sum = 0;
        for(ClassificationEvaluation evaluation :evaluations){
            sum+= evaluation.getTruePositive();
        }
        return sum;
    }
    public long getTrueNegative(){
        long sum = 0;
        for(ClassificationEvaluation evaluation :evaluations){
            sum+= evaluation.getTrueNegative();
        }
        return sum;
    }
    public long getFalseNegative(){
        long sum = 0;
        for(ClassificationEvaluation evaluation :evaluations){
            sum+= evaluation.getFalseNegative();
        }
        return sum;
    }
    public long getFalsePositive(){
        long sum = 0;
        for(ClassificationEvaluation evaluation :evaluations){
            sum+= evaluation.getFalsePositive();
        }
        return sum;
    }

    public long getPositive(){
        long sum = 0;
        for(ClassificationEvaluation evaluation :evaluations){
            sum+= evaluation.getPositive();
        }
        return sum;
    }

    public long getNegative(){
        long sum = 0;
        for(ClassificationEvaluation evaluation :evaluations){
            sum+= evaluation.getNegative();
        }
        return sum;
    }


    public BigDecimal accuracy(Mode mode){
        if(mode == Mode.AVERAGE){
            BigDecimal sum = BigDecimal.ZERO;
            for(ClassificationEvaluation evaluation :evaluations){
                sum = sum.add(evaluation.accuracy());
            }
            return sum.divide(new BigDecimal(evaluations.length), scale,RoundingMode.HALF_UP);
        }else{
            return new ClassificationEvaluation(getTruePositive(), getTrueNegative() , getFalseNegative() , getFalsePositive()).accuracy();
        }
    }

    public BigDecimal f1Score(Mode mode){
        if(mode == Mode.AVERAGE){
            BigDecimal sum = BigDecimal.ZERO;
            for(ClassificationEvaluation evaluation :evaluations){
                sum = sum.add(evaluation.f1Score());
            }
            return sum.divide(new BigDecimal(evaluations.length), scale,RoundingMode.HALF_UP);
        }else{
            return new ClassificationEvaluation(getTruePositive(), getTrueNegative() , getFalseNegative() , getFalsePositive()).f1Score();
        }
    }


    public BigDecimal geometricMean(Mode mode){
        if(mode == Mode.AVERAGE){
            BigDecimal sum = BigDecimal.ZERO;
            for(ClassificationEvaluation evaluation :evaluations){
                sum = sum.add(evaluation.geometricMean());
            }
            return sum.divide(new BigDecimal(evaluations.length), scale,RoundingMode.HALF_UP);
        }else{
            return new ClassificationEvaluation(getTruePositive(), getTrueNegative() , getFalseNegative() , getFalsePositive()).geometricMean();
        }
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

        jsonObject.addProperty("accuracy_average", accuracy(Mode.AVERAGE));
        jsonObject.addProperty("accuracy_sum", accuracy(Mode.SUM));
        jsonObject.addProperty("f1-score_average", f1Score(Mode.AVERAGE));
        jsonObject.addProperty("f1-score_sum", f1Score(Mode.SUM));
        jsonObject.addProperty("geometric_mean_average", geometricMean(Mode.AVERAGE));
        jsonObject.addProperty("geometric_mean_average", geometricMean(Mode.SUM));

        jsonObject.addProperty("p", getPositive());
        jsonObject.addProperty("n", getNegative());
        jsonObject.addProperty("tp", getTruePositive());
        jsonObject.addProperty("tn", getTrueNegative());
        jsonObject.addProperty("fn", getFalseNegative());
        jsonObject.addProperty("fp", getFalsePositive());


        JsonArray array = new JsonArray();
        for(ClassificationEvaluation evaluation :evaluations){
            array.add(evaluation.toJsonObject());
        }

        jsonObject.add("evaluations", array);

        return jsonObject;
    }

}

package io.runon.classification.evaluation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 훈련과 테스트 평가정보
 * 다항분류 기준일때
 * @author macle
 */
public class TradingTestEvaluation extends MultinomialEvaluation{

    private final MultinomialEvaluation tradingEvaluation, testEvaluation;

    public TradingTestEvaluation(ClassificationEvaluation [] evaluations) {
        super(evaluations);

        tradingEvaluation = new MultinomialEvaluation(copy(evaluations));
        testEvaluation = new MultinomialEvaluation(copy(evaluations));
    }

    @Override
    public void setScale(int scale) {
       super.setScale(scale);
       tradingEvaluation.setScale(scale);
       testEvaluation.setScale(scale);
    }

    @Override
    public void setId(String id) {
        this.id = id;
        tradingEvaluation.setId(id);
        testEvaluation.setId(id);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        tradingEvaluation.setName(name);
        testEvaluation.setName(name);
    }

    private ClassificationEvaluation [] copy(ClassificationEvaluation [] evaluations){
        ClassificationEvaluation [] copy = new ClassificationEvaluation[evaluations.length];
        for (int i = 0; i <copy.length ; i++) {
            copy[i] = new ClassificationEvaluation();
            copy[i].setId(evaluations[i].getId());
            copy[i].setName(evaluations[i].getName());
        }
        return copy;
    }

    public void add(boolean isTrading, String classificationId, String trueId ){
        this.add(classificationId, trueId);
        if(isTrading){
            tradingEvaluation.add(classificationId, trueId);
        }else{
            testEvaluation.add(classificationId, trueId);
        }
    }

    public MultinomialEvaluation getTradingEvaluation() {
        return tradingEvaluation;
    }

    public MultinomialEvaluation getTestEvaluation() {
        return testEvaluation;
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

        if(tradingEvaluation.length() == 0 && testEvaluation.length() == 0){
            return jsonObject;
        }

        if(testEvaluation.length() == 0){
            return tradingEvaluation.toJsonObject();
        }else if(tradingEvaluation.length() == 0){
            return testEvaluation.toJsonObject();
        }

        JsonObject total = new JsonObject();
        setJsonObject(total);
        jsonObject.add("total", total);
        JsonObject trading = new JsonObject();
        tradingEvaluation.setJsonObject(trading);
        jsonObject.add("trading", trading);
        JsonObject test = new JsonObject();
        testEvaluation.setJsonObject(test);
        jsonObject.add("test", test);


        JsonArray array = new JsonArray();
        for (int i = 0; i <evaluations.length ; i++) {
            JsonObject evaluation = new JsonObject();
            JsonObject totalJson = new JsonObject();

            if(evaluations[i].length() ==0){
                continue;
            }
            if(evaluations[i].id != null){
                totalJson.addProperty("id",evaluations[i].id);
            }
            if(evaluations[i].name != null){
                totalJson.addProperty("name", evaluations[i].name);
            }

            evaluations[i].setJsonObject(totalJson);
            evaluation.add("total", totalJson);

            if(tradingEvaluation.evaluations[i].length() > 0) {
                JsonObject tradingJson = new JsonObject();
                tradingEvaluation.evaluations[i].setJsonObject(tradingJson);
                evaluation.add("trading", tradingJson);
            }

            if(testEvaluation.evaluations[i].length() > 0) {
                JsonObject testJson = new JsonObject();
                testEvaluation.evaluations[i].setJsonObject(testJson);
                evaluation.add("test", testJson);
            }


            array.add(evaluation);
        }
        jsonObject.add("evaluations", array);

        return jsonObject;
    }

}

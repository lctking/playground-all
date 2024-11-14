package com.nullwli.playground.frameworks.starter.convention.result;

public class Results {

    public static Result<Void> success(){
        return new Result<Void>().setCode(Result.SUCCESS_CODE);
    }


    public static <T> Result<T> success(T data){
        return new Result<T>()
                .setData(data)
                .setCode(Result.SUCCESS_CODE);
    }

    public static Result<Void> fail(){
        return new Result<Void>().setCode("ERROR");
    }


}

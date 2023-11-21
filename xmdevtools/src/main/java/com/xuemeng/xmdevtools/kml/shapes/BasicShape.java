package com.xuemeng.xmdevtools.kml.shapes;


import com.xuemeng.xmdevtools.kml.C;

/**
 * Created by Edvinas on 2016-02-10.
 */
public abstract class BasicShape {

    public String mName;
    public String mDescription;
    public C.Type mType;

    protected String addName(){
        if(mName != null && !mName.trim().isEmpty()){
            return "<name>" + mName + "</name>\n";
        }
        return "";
    }

    protected String addDescription(){
        if(mDescription != null && !mDescription.trim().isEmpty()){
            return "<description>" + mDescription + "</description>\n";
        }
        return "";
    }

    public C.Type getType() {
        return mType;
    }

    public void setType(C.Type mType) {
        this.mType = mType;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    protected static abstract  class Builder<T extends BasicShape, B extends Builder<T, B>>  {
        protected T object;
        protected B thisObject;
        protected abstract T createObject();
        protected abstract B thisObject();


        public Builder() {
            object = createObject();
            thisObject = thisObject();
        }

        public B name(String name){
            object.mName = name;
            return thisObject;
        }

        public B description(String description){
            object.mDescription = description;
            return thisObject;
        }

        public T build(){
            return object;
        }
    }

    @Override
    public String toString() {
        return mName;
    }
}

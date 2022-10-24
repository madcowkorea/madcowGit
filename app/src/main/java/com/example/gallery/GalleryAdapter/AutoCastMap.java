package com.example.gallery.GalleryAdapter;

import java.util.HashMap;

public class AutoCastMap extends HashMap {

    public AutoCastMap() {
        super();
    }

    public Object put(Object key, Object value) {

        String newValue = "";
        //String newkey = key.toString().toLowerCase();    //소문자로 변환
        String newkey = key.toString();

        if (value != null) {

            if (value.getClass().getName().contains("ArrayList") || value.getClass().getName().contains("AutoCastMap")) {
                return super.put(newkey, value);
            } else {
                newValue = value.toString();

//				if(newValue.indexOf("'") > -1) {
//					newValue = newValue.replaceAll("'", "&#39;");
//				} else {
                newValue = newValue;
//				}

                return super.put(newkey, newValue);
            }

        } else {
            return super.put(newkey, newValue);
        }

    }

    public Object get(Object key) {

        String value = "";

        if (super.get(key) != null) {

            if (super.get(key).getClass().getName().contains("ArrayList") || super.get(key).getClass().getName().contains("AutoCastMap")) {
                return super.get(key);
            } else {
                value = super.get(key).toString();

//				if(value.indexOf("&#39;") > -1) {
//					value = value.replaceAll("&#39;", "\'");
//				}

                return value;
            }
        } else {
            return value;
        }


    }


    public Object putParam(Object key, Object value) {

        String newValue = "";

        if (value != null) {
            newValue = value.toString();
        }

        String newkey = key.toString();

//		if(newValue.indexOf("'") > -1) {
//			newValue = newValue.replaceAll("'", "&#39;");
//		} else {
        newValue = newValue;
//		}

        return super.put(newkey, newValue);
    }

    public int getInt(Object key) {

        int value = 0;

        if (super.get(key) != null) {
            if (!"".equals(super.get(key).toString())) {
                value = Integer.parseInt(super.get(key).toString());
            }
        }

        return value;
    }

    public float getFloat(Object key) {

        float value = (float) 0;

        if (super.get(key) != null) {
            if (!"".equals(super.get(key).toString())) {
                value = Float.parseFloat(super.get(key).toString());
            }
        }

        return value;
    }

    public long getLong(Object key) {

        long value = 0;

        if (super.get(key) != null) {
            if (!"".equals(super.get(key).toString())) {
                value = Long.parseLong(super.get(key).toString());
            }
        }

        return value;
    }

    public double getDouble(Object key) {

        double value = 0;

        if (super.get(key) != null) {
            if (!"".equals(super.get(key).toString())) {
                value = Double.parseDouble(super.get(key).toString());
            }
        }

        return value;
    }

    public Boolean getBoolean(Object key) {

        boolean value = false;
        if(super.get(key) == null) return value;

        String sValue = super.get(key).toString();

        if (super.get(key) != null) {
            if (!"".equals(sValue) && ("true".equals(sValue) || "false".equals(sValue))) {
                value = Boolean.parseBoolean(sValue);
            }
        }

        return value;
    }

    public String getString(Object key) {

        return (String) get(key);
    }

}

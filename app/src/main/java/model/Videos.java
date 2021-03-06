package model;

import java.util.List;

/**
 * Created by 123 on 2016/9/21.
 */

public class Videos {


    /**
     * id : 550
     * results : [{"id":"533ec654c3a36854480003eb","iso_639_1":"en","key":"SUXWAEX2jlg","name":"Trailer 1","site":"YouTube","size":720,"type":"Trailer"}]
     */

    private int id;
    /**
     * id : 533ec654c3a36854480003eb
     * iso_639_1 : en
     * key : SUXWAEX2jlg
     * name : Trailer 1
     * site : YouTube
     * size : 720
     * type : Trailer
     */

    private List<ResultsBean> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        private String id;
        private String iso_639_1;
        private String key;
        private String name;
        private String site;
        private int size;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

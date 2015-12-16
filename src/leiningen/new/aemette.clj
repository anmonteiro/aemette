(ns leiningen.new.aemette
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "aemette"))

(def valid-opts ["+next"])

(defn get-unsupported-opts [opts]
  (clojure.set/difference (set opts) (set valid-opts)))

(defn valid-opts? [opts]
  (-> opts
      get-unsupported-opts
      empty?))

(defn om-next? [opts]
  (some #{"+next"} opts))

(defn common-files [data]
  [["project.clj" (render "project.clj" data)]
   [".gitignore" (render "gitignore" data)]
   ["resources/public/index.html" (render "index.html" data)]
   ["README.md" (render "README.md" data)]
   ["src/{{sanitized}}/history.cljs" (render "history.cljs" data)]
   ["src/{{sanitized}}/routes.cljs" (render "routes.cljs" data)]])

(defn om-files [data]
  [["src/{{sanitized}}/core.cljs" (render "om/core.cljs" data)]
   ["src/{{sanitized}}/components/app.cljs" (render "om/components_app.cljs" data)]
   ["src/{{sanitized}}/components/landing.cljs" (render "om/components_landing.cljs" data)]
   ["src/{{sanitized}}/controllers/navigation.cljs" (render "om/controllers_navigation.cljs" data)]])

(defn om-next-files [data]
  [["src/{{sanitized}}/core.cljs" (render "next/core.cljs" data)]
   ["src/{{sanitized}}/components/app.cljs" (render "next/components_app.cljs" data)]
   ["src/{{sanitized}}/components/landing.cljs" (render "next/components_landing.cljs" data)]
   ["src/{{sanitized}}/controllers/navigation.cljs" (render "next/controllers_navigation.cljs" data)]
   ["src/{{sanitized}}/parser.cljs" (render "next/parser.cljs" data)]])

(defn opts->files [name opts]
  (let [data {:name name
              :sanitized (name-to-path name)
              :om-legacy (not (om-next? opts))}
        arg-dependent-files (cond
                              ;; default to om
                              (empty? opts) (om-files data)
                              (om-next? opts) (om-next-files data))
        files (into (common-files data)
                    arg-dependent-files)]
    (into [data] files)))

(defn aemette
  [name & opts]
  (main/info "Generating fresh 'lein new' aemette project.")
  (if (valid-opts? opts)
    (apply ->files (opts->files name opts))
    (main/info "Invalid opts" (apply str (interpose ", " (get-unsupported-opts opts))))))

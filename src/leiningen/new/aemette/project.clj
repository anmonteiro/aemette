(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 {{#om-legacy}}[org.omcljs/om "0.9.0"]{{/om-legacy}}
                 {{#om-next}}[org.omcljs/om "1.0.0-alpha28"]{{/om-next}}
                 [org.clojure/core.async "0.2.374"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]

  :source-paths ["src"]
  :clean-targets ^{:protect false} [[:cljsbuild :builds 0 :compiler :output-dir]
                                    [:cljsbuild :builds 0 :compiler :output-to]
                                    "target"]
  :figwheel {:css-dirs ["resources/public/css"]}
  :cljsbuild {:builds [{:id "dev"
                        :figwheel true
                        :source-paths ["src"]
                        :compiler {:main {{name}}.core
                                   :asset-path "js/out"
                                   :output-to "resources/public/js/{{name}}.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "production"
                        :source-paths ["src"]
                        :compiler {:main {{name}}.core
                                   :output-to "resources/public/js/{{name}}.js"
                                   :optimizations :advanced
                                   :elide-asserts true
                                   :pretty-print false}}]})

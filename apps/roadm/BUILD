COMPILE_DEPS = CORE_DEPS + JACKSON + NETTY + CLI + [
    "@json//jar",
]

BUNDLES = [
    "//apps/roadm/app:onos-apps-roadm-app",
    "@json//jar",
]
osgi_jar_with_tests(
    deps = COMPILE_DEPS,
)

onos_app(
    category = "Graphical User Interface",
    description = "ONOS OSGi GUI2 Custom-View app roadm.",
    included_bundles = BUNDLES,
    title = "Optical Application",
    url = "http://onosproject.org",
)

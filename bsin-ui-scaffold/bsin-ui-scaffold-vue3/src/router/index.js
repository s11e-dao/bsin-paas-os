const routes = [
  {
    path: "/vue3",
    name: "home",
    component: () => import(/* webpackChunkName: "home" */ "@/views/Home.vue"),
  },
  // {
  //   path: "/bsin-ui-upms",
  //   name: "About",
  //   component: () =>
  //     import(/* webpackChunkName: "about" */ "../views/About.vue"),
  //   children: [
  //     {
  //       path: "organization-post",
  //       name: "About",
  //       component: () =>
  //         import(/* webpackChunkName: "about" */ "../views/About.vue"),
  //     },
  //   ],
  // },
];

export default routes;

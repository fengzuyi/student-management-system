import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '../layout/index.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    component: () => import('../views/login/Login.vue'),
    meta: { 
      title: '学生管理系统',
      hidden: true,
      notNeedAuth: true 
    }
  },
  {
    path: '/404',
    component: () => import('../views/404.vue'),
    meta: { 
      title: '学生管理系统',
      hidden: true,
      notNeedAuth: true 
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/student-management/student-list',
    children: [
      {
        path: 'student-management',
        name: 'StudentManagement',
        component: () => import('../views/student-management/index.vue'),
        meta: { 
          title: '学生管理系统',
          icon: 'School'
        },
        children: [
          {
            path: 'student-list',
            name: 'StudentList',
            component: () => import('../views/student/StudentList.vue'),
            meta: { 
              title: '学生管理',
              icon: 'User'
            }
          },
          {
            path: 'class-list',
            name: 'ClassList',
            component: () => import('../views/class/ClassList.vue'),
            meta: { 
              title: '班级管理',
              icon: 'House'
            }
          },
          {
            path: 'course-list',
            name: 'CourseList',
            component: () => import('../views/course/index.vue'),
            meta: { 
              title: '课程管理',
              icon: 'Reading'
            }
          },
          {
            path: 'grade-management',
            name: 'GradeManagement',
            component: () => import('../views/grade/GradeManagement.vue'),
            meta: {
              title: '成绩管理',
              icon: 'Document',
              roles: ['ADMIN', 'TEACHER']
            }
          },
          {
            path: 'user-list',
            name: 'UserList',
            component: () => import('../views/user/UserList.vue'),
            meta: {
              title: '用户管理',
              icon: 'UserFilled',
              roles: ['ADMIN']
            }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: { hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes,
  scrollBehavior: (to, from, savedPosition) => {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0, left: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title as string || '学生管理系统'

  // 不需要认证的页面直接放行
  if (to.meta.notNeedAuth) {
    next()
    return
  }

  // 检查是否已登录
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }

  // 检查权限
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const roles = to.meta.roles as string[] | undefined
  if (roles && !roles.includes(userInfo.role)) {
    next('/404')
    return
  }

  next()
})

export default router 
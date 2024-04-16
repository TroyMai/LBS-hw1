<template>
<!--  <nav>-->
<!--    <router-link to="/">Home</router-link> |-->
<!--    <router-link to="/user">User</router-link>-->
<!--  </nav>-->
  <div>
    <Header/>
    <div style="display: flex; justify-content: center">
      <RouterView style="width: 1000px"/>
    </div>
  </div>
</template>

<style>
</style>

<script>
import Header from '@/components/Header.vue'
// 解决ERROR ResizeObserver loop completed with undelivered notifications.

//问题的

const debounce = (fn, delay) => {

  let timer = null;

  return function () {

    let context = this;

    let args = arguments;

    clearTimeout(timer);

    timer = setTimeout(function () {

      fn.apply(context, args);

    }, delay);

  }

}

// 解决ERROR ResizeObserver loop completed with undelivered notifications.

//问题的

const _ResizeObserver = window.ResizeObserver;

window.ResizeObserver = class ResizeObserver extends _ResizeObserver {

  constructor(callback) {

    callback = debounce(callback, 16);

    super(callback);

  }

}
export default {
  name: 'App',
  components: {
    Header,
  },
}
</script>
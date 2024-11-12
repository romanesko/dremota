<div>
  {#if loading}
    <div></div>
  {:else}
    {#if userAuthorized}
      <MainLayout/>
    {:else}
      <LoginPage/>
    {/if}
  {/if}

</div>


<script lang="ts">


    import LoginPage from './components/LoginPage.svelte'

    import {auth_token} from './store/auth'

    import MainLayout from "./components/MainLayout.svelte";

    import api from '@/lib/api';
    import {currentUser} from "@/store/user";
    import {log} from "@/lib/common";


    let loading = $state(true);

  let userAuthorized = $state(false)

  $effect(() => {
    if (auth_token) {
      api.getState().then(a => {
        userAuthorized = true;
        loading = false;
      }).catch(e => {
        log('un-authorized', e)
        userAuthorized = false;
        loading = false;
      })
    } else {
      userAuthorized = false;
      loading = false;
    }
  });


  $effect(() => {
    if (userAuthorized && !$currentUser){
      api.getMe().then(a => {
        currentUser.set(a)
      })
    }
  });


</script>
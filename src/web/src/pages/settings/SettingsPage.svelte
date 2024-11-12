<div class="col-12">


  <div class="card">
    <div class="card-header">
      <ul class="nav nav-tabs card-header-tabs" data-bs-toggle="tabs" role="tablist">
        {#each tabs as tab}
        <li class="nav-item" role="presentation">
          <a href="/#/settings/{tab.id}" class="nav-link" class:active={activeTabId===tab.id} aria-selected="true" role="tab">{tab.name}</a>
        </li>
        {/each}
      </ul>
    </div>
    <div class="card-body">
      <div class="tab-content">
        {#each tabs as tab}
        <div class="tab-pane show"  class:active={activeTabId===tab.id}  role="tabpanel">
          <div class="row">
            <svelte:component this={tab.component}/>
          </div>
        </div>
          {/each}


      </div>
    </div>
  </div>

</div>



<script lang="ts">
    import Commands from "@/pages/settings/components/Commands.svelte";
    import BaseSettings from "@/pages/settings/components/BaseSettings.svelte";
    import Price from "@/pages/settings/components/Price.svelte";
    import ReferralProgram from "@/pages/settings/components/ReferralProgram.svelte";

    const tabs = [
    {id: 'base', name: 'Базовые настройки', component: BaseSettings},
    {id: 'commands', name: 'Команды', component: Commands},
    {id: 'price', name: 'Прайс', component: Price},
    {id: 'referral', name: 'Реферальная программа', component: ReferralProgram}
  ]


  let {params}: { params: { tab: string } } = $props()

  let activeTabId = $state(params?.tab || 'base')

  $effect(() => {
    if (params?.tab) {
      activeTabId = params.tab
    }
  })

</script>

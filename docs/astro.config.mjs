// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

// https://astro.build/config
export default defineConfig({
	integrations: [
		starlight({
			title: 'Enchantio Wiki',
			social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/YouHaveTrouble/Enchantio' }],
			sidebar: [
				{
					label: 'Documentation',
					items: [{ autogenerate: { "directory": "documentation" } }]
				},
				{
					label: 'Configuration',
					items: [
						{ label: 'Reference', slug: 'configuration/reference' },
						{ label: 'Input types', slug: 'configuration/input-types' },
					],
				},
			],
		}),
	],
});
